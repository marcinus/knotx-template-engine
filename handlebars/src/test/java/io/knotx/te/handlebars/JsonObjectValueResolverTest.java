/*
 * Copyright (C) 2018 Knot.x Project
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
package io.knotx.te.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.knotx.junit5.util.FileReader;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonObjectValueResolverTest {

  private String expected;
  private Template template;

  @BeforeEach
  void before() throws Exception {
    template = new Handlebars()
        .compileInline(FileReader.readText("templates/handlebars-template.hbs"));
    expected = FileReader.readText("results/simple").trim();
  }

  @Test
  void JsonObjectResolver_whenApplyingObject_expectVariablesResolved()
      throws Exception {
    Context context = Context
        .newBuilder(new JsonObject(FileReader.readText("data/sampleContext.json")))
        .push(JsonObjectValueResolver.INSTANCE)
        .build();
    String compiled = template.apply(context).trim();

    assertEquals(expected, compiled);
  }


}
