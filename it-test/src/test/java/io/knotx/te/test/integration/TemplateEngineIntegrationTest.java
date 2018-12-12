package io.knotx.te.test.integration;

import static io.knotx.junit5.util.RequestUtil.subscribeToResult_shouldSucceed;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.reactivex.proxy.KnotProxy;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class TemplateEngineIntegrationTest {

  private static final String CORE_MODULE_EB_ADDRESS = "knotx.knot.te";

  @Test
  @KnotxApplyConfiguration("templateEngineStack.conf")
  void callTemplateEngine_validateResult(VertxTestContext context, Vertx vertx)
      throws IOException, URISyntaxException {

    callWithAssertions(context, vertx, "snippet/simple-handlebars.txt", "data/simple.json",
        knotContext -> {
          final String expectedMarkup = fileContentAsString("result/simple.txt");
          final String markup = knotContext.getFragments().iterator().next().content();
          final boolean failed = knotContext.getFragments().iterator().next().failed();

          assertFalse(failed);
          assertThat(markup, equalToIgnoringWhiteSpace(expectedMarkup));
        });
  }

  @Test
  @KnotxApplyConfiguration("templateEngineStack.conf")
  void callTemplateEngine_validateFallback(VertxTestContext context, Vertx vertx)
      throws IOException, URISyntaxException {

    callWithAssertions(context, vertx, "snippet/simple-missing-engine.txt", "data/simple.json",
        knotContext -> {
          final boolean failed = knotContext.getFragments().iterator().next().failed();

          assertTrue(failed);
        });
  }

  private void callWithAssertions(
      VertxTestContext context, Vertx vertx, String snippetPath, String dataPath,
      Consumer<KnotContext> onSuccess) throws IOException, URISyntaxException {
    KnotContext message = buildContext(snippetPath, dataPath);

    rxProcessWithAssertions(context, vertx, onSuccess, message);
  }

  private void rxProcessWithAssertions(VertxTestContext context, Vertx vertx,
      Consumer<KnotContext> onSuccess, KnotContext payload) {
    KnotProxy service = KnotProxy.createProxy(vertx, CORE_MODULE_EB_ADDRESS);
    Single<KnotContext> knotContextSingle = service.rxProcess(payload);

    subscribeToResult_shouldSucceed(context, knotContextSingle, onSuccess);
  }

  private KnotContext buildContext(String snippetPath, String dataPath)
      throws IOException, URISyntaxException {
    String fragmentContent = fileContentAsString(snippetPath);
    JsonObject data = new JsonObject(fileContentAsString(dataPath));

    final Fragment fragment = fragmentContent.contains("fallback")? Fragment
        .snippet(Collections.singletonList("te"), fragmentContent, "BLANK") :  Fragment
        .snippet(Collections.singletonList("te"), fragmentContent);
    fragment.context().mergeIn(new JsonObject(Collections.singletonMap("_result", data)));

    return new KnotContext()
        .setClientRequest(new ClientRequest())
        .setFragments(Collections.singletonList(fragment));
  }

  private String fileContentAsString(String filePath) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
        .getResource(filePath).toURI())));
  }
}
