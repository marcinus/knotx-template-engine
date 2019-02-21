package io.knotx.te.test.integration;

import static io.knotx.junit5.util.RequestUtil.subscribeToResult_shouldSucceed;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import io.knotx.engine.api.FragmentEvent;
import io.knotx.engine.api.FragmentEvent.Status;
import io.knotx.engine.api.FragmentEventContext;
import io.knotx.engine.api.FragmentEventResult;
import io.knotx.engine.api.KnotFlow;
import io.knotx.engine.reactivex.api.KnotProxy;
import io.knotx.fragment.Fragment;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.junit5.util.FileReader;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.te.core.TemplateEngineKnot;
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
  @DisplayName("Expect merged snippet template and dynamic data, when using default te")
  @KnotxApplyConfiguration("templateEngineStack.conf")
  void callDefaultTe(VertxTestContext context, Vertx vertx)
      throws IOException {

    callWithAssertions(context, vertx, "snippet/simple-handlebars.txt", "data/simple.json",
        new KnotFlow(TemplateEngineKnot.EB_ADDRESS, Collections.emptyMap()),
        fer -> {
          final String expectedMarkup = fileContentAsString("result/simple.txt");
          final String markup = fer.getFragmentEvent().getFragment().getBody();

          assertEquals(Status.SUCCESS, fer.getFragmentEvent().getStatus());
          assertThat(markup, equalToIgnoringWhiteSpace(expectedMarkup));
        });
  }

  @Test
  @DisplayName("Expect merged snippet template and dynamic data, when using te strategy")
  @KnotxApplyConfiguration("templateEngineStack.conf")
  void callHandlebars(VertxTestContext context, Vertx vertx)
      throws IOException {

    callWithAssertions(context, vertx, "snippet/simple-handlebars.txt", "data/simple.json",
        "handlebars", new KnotFlow(TemplateEngineKnot.EB_ADDRESS, Collections.emptyMap()),
        fer -> {
          final String expectedMarkup = fileContentAsString("result/simple.txt");
          final String markup = fer.getFragmentEvent().getFragment().getBody();

          assertEquals(Status.SUCCESS, fer.getFragmentEvent().getStatus());
          assertThat(markup, equalToIgnoringWhiteSpace(expectedMarkup));
        });
  }

  @Test
  @DisplayName("Expect failed processing when non existing te called")
  @KnotxApplyConfiguration("templateEngineStack.conf")
  void callNonExistingEngine(VertxTestContext context, Vertx vertx)
      throws IOException {

    callWithAssertions(context, vertx, "snippet/simple-handlebars.txt", "data/simple.json",
        "non-existing",
        new KnotFlow(TemplateEngineKnot.EB_ADDRESS, Collections.emptyMap()),
        fer -> assertEquals(Status.FAILURE, fer.getFragmentEvent().getStatus()));
  }

  private void callWithAssertions(VertxTestContext context, Vertx vertx,
      String bodyPath, String payloadPath, String teStrategy, KnotFlow flow,
      Consumer<FragmentEventResult> onSuccess) throws IOException {
    FragmentEventContext message = payloadMessage(bodyPath, payloadPath, teStrategy, flow);

    rxProcessWithAssertions(context, vertx, onSuccess, message);
  }

  private void callWithAssertions(VertxTestContext context, Vertx vertx,
      String bodyPath, String payloadPath, KnotFlow flow, Consumer<FragmentEventResult> onSuccess)
      throws IOException {
    callWithAssertions(context, vertx, bodyPath, payloadPath, null, flow, onSuccess);
  }

  private void rxProcessWithAssertions(VertxTestContext context, Vertx vertx,
      Consumer<FragmentEventResult> onSuccess, FragmentEventContext payload) {
    KnotProxy service = KnotProxy.createProxy(vertx, TemplateEngineKnot.EB_ADDRESS);
    Single<FragmentEventResult> SnippetFragmentsContextSingle = service.rxProcess(payload);

    subscribeToResult_shouldSucceed(context, SnippetFragmentsContextSingle, onSuccess);
  }

  private FragmentEventContext payloadMessage(String bodyPath, String payloadPath,
      String teStrategy,
      KnotFlow flow)
      throws IOException {
    return new FragmentEventContext(
        new FragmentEvent(fromJsonFiles(bodyPath, payloadPath, teStrategy), flow),
        new ClientRequest(), 0);
  }

  private Fragment fromJsonFiles(String bodyPath, String payloadPath, String teStrategy)
      throws IOException {
    final String body = FileReader.readText(bodyPath);
    final String payload = FileReader.readText(payloadPath);

    final JsonObject configuration = new JsonObject().put("knots", "te");
    if (teStrategy != null) {
      configuration.put("te-strategy", teStrategy);

    }
    final Fragment fragment = new Fragment("snippet", configuration, body);
    fragment.mergeInPayload(
        new JsonObject(Collections.singletonMap("_result", new JsonObject(payload))));
    return fragment;
  }

  private String fileContentAsString(String filePath) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
        .getResource(filePath).toURI())));
  }
}
