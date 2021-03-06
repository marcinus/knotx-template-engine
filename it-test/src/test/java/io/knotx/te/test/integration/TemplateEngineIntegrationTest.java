/*
 * Copyright (C) 2019 Knot.x Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.te.test.integration;

import static io.knotx.junit5.assertions.KnotxAssertions.assertEqualsIgnoreWhitespace;
import static io.knotx.junit5.util.RequestUtil.subscribeToResult_shouldSucceed;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.knotx.fragments.api.Fragment;
import io.knotx.fragments.handler.api.domain.FragmentContext;
import io.knotx.fragments.handler.api.domain.FragmentResult;
import io.knotx.fragments.handler.reactivex.api.Knot;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.junit5.util.FileReader;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.te.core.TemplateEngineKnotOptions;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
class TemplateEngineIntegrationTest {

  @Test
  @DisplayName("Expect merged snippet template and dynamic data using handlebars")
  @KnotxApplyConfiguration("templateEngineStack.conf")
  void callHandlebars(VertxTestContext context, Vertx vertx) throws IOException {
    callWithAssertions(context, vertx, "snippet/simple-handlebars.txt", "data/simple.json",
        fragmentResult -> {
          final String expectedMarkup = fileContentAsString("result/simple.txt");
          final String markup = fragmentResult.getFragment().getBody();

          assertEquals(FragmentResult.SUCCESS_TRANSITION, fragmentResult.getTransition());
          assertEqualsIgnoreWhitespace(expectedMarkup, markup);
        });
  }

  private void callWithAssertions(VertxTestContext context, Vertx vertx,
      String bodyPath, String payloadPath, Consumer<FragmentResult> onSuccess) throws IOException {
    FragmentContext message = payloadMessage(bodyPath, payloadPath);
    rxProcessWithAssertions(context, vertx, onSuccess, message);
  }

  private void rxProcessWithAssertions(VertxTestContext context, Vertx vertx,
      Consumer<FragmentResult> onSuccess, FragmentContext payload) {
    Knot service = Knot.createProxy(vertx, TemplateEngineKnotOptions.DEFAULT_EB_ADDRESS);
    Single<FragmentResult> fragmentResult = service.rxApply(payload);

    subscribeToResult_shouldSucceed(context, fragmentResult, onSuccess);
  }

  private FragmentContext payloadMessage(String bodyPath, String payloadPath) throws IOException {
    return new FragmentContext(fromJsonFiles(bodyPath, payloadPath), new ClientRequest());
  }

  private Fragment fromJsonFiles(String bodyPath, String payloadPath) throws IOException {
    final String body = FileReader.readText(bodyPath);
    final String payload = FileReader.readText(payloadPath);
    final Fragment fragment = new Fragment("snippet", new JsonObject(), body);
    fragment.mergeInPayload(
        new JsonObject(Collections.singletonMap("_result", new JsonObject(payload))));
    return fragment;
  }

  private String fileContentAsString(String filePath) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
        .getResource(filePath).toURI())));
  }

}
