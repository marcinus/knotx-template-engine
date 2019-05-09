package io.knotx.te.handlebars;

import static io.knotx.junit5.assertions.KnotxAssertions.assertEqualsIgnoreWhitespace;
import static org.mockito.Mockito.when;

import io.knotx.fragment.Fragment;
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
    final String expected = FileReader.readText("results/simple").trim();
    assertEqualsIgnoreWhitespace(expected, result);
  }

  @Test
  void process_whenCustomDelimiter_expectMarkup() throws IOException {
    options.setStartDelimiter("<&").setEndDelimiter("&>");
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);

    final Fragment fragment = mockFragmentFromFile("templates/simple-customDelimiter.hbs",
        "data/sampleContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/customDelimiter").trim();
    assertEqualsIgnoreWhitespace(expected, result);
  }

  @Test
  void process_whenEmptyContext_expectMarkup() throws IOException {
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);
    final Fragment fragment = mockFragmentFromFile("templates/simple.hbs",
        "data/emptyContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/emptyContext").trim();
    assertEqualsIgnoreWhitespace(expected, result);
  }

  @Test
  void process_whenEmptyContent_expectMarkup() throws IOException {
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);
    final Fragment fragment = mockFragmentFromFile("templates/empty.hbs",
        "data/sampleContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/emptyContent").trim();
    assertEqualsIgnoreWhitespace(expected, result);
  }

  @Test
  void process_whenUndefinedHbsHelperSpotted_expectMarkup() throws IOException {
    final HandlebarsTemplateEngine templateEngine = new HandlebarsTemplateEngine(vertx, options);
    final Fragment fragment = mockFragmentFromFile("templates/undefinedHelper.hbs",
        "data/sampleContext.json");
    final String result = templateEngine.process(fragment).trim();
    final String expected = FileReader.readText("results/undefinedHelper").trim();
    assertEqualsIgnoreWhitespace(expected, result);
  }

  private Fragment mockFragmentFromFile(String bodyFilePath, String contextFilePath)
      throws IOException {
    final String body = FileReader.readText(bodyFilePath).trim();
    final String context = FileReader.readText(contextFilePath).trim();

    final Fragment mockedFragment = Mockito.mock(Fragment.class);
    when(mockedFragment.getBody()).thenReturn(body);
    when(mockedFragment.getPayload()).thenReturn(new JsonObject(context));

    return mockedFragment;
  }

}
