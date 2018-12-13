package io.knotx.te.handlebars;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.mockito.Mockito.when;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit5.util.FileReader;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class HandlebarsTemplateEngineTest {

  /**
   * null, HandlebarsTemplateEngine does not use Vert.x
   */
  private Vertx vertx;
  private HandlebarsEngineOptions options;

  @BeforeEach
  void setUp() {
    options = new HandlebarsEngineOptions().setCacheSize(100L);
  }

  @Test
  void process_whenDefaultOptions_expectMarkup() throws IOException {
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);

    final Fragment fragment = mockFragmentFromFile("templates/simple.hbs",
        "data/sampleContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/expected").trim();
    assertThat(result, equalToIgnoringWhiteSpace(expected));
  }

  @Test
  void process_whenCustomDelimiter_expectMarkup() throws IOException {
    options.setStartDelimiter("<&").setEndDelimiter("&>");
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);

    final Fragment fragment = mockFragmentFromFile("templates/simple-customDelimiter.hbs",
        "data/sampleContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/expected-customDelimiter").trim();
    assertThat(result, equalToIgnoringWhiteSpace(expected));
  }

  @Test
  void process_whenEmptyContext_expectMarkup() throws IOException {
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);
    final Fragment fragment = mockFragmentFromFile("templates/simple.hbs",
        "data/emptyContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/expected-emptyContext").trim();
    assertThat(result, equalToIgnoringWhiteSpace(expected));
  }

  private Fragment mockFragmentFromFile(String contentFilePath, String contextFilePath)
      throws IOException {
    final String content = FileReader.readText(contentFilePath).trim();
    final String context = FileReader.readText(contextFilePath).trim();

    final Fragment mockedFragment = Mockito.mock(Fragment.class);
    when(mockedFragment.content()).thenReturn(content);
    when(mockedFragment.context()).thenReturn(new JsonObject(context));
    when(mockedFragment.isRaw()).thenReturn(false);

    return mockedFragment;
  }
}
