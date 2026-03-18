package ai.rhesis.sdk.synthesizers;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Prompt;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
import ai.rhesis.sdk.models.ChatRequest;
import ai.rhesis.sdk.models.ChatResponse;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.loader.ResourceLocator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseSynthesizer {
  protected final Jinjava jinjava;
  protected final int batchSize;
  protected final ChatModelClient modelClient;

  public BaseSynthesizer(ChatModelClient modelClient, int batchSize) {
    this.modelClient = modelClient != null ? modelClient : RhesisClient.getDefault().models();
    this.jinjava = new Jinjava();
    this.jinjava.setResourceLocator(
        new ResourceLocator() {
          @Override
          public String getString(
              String fullName,
              java.nio.charset.Charset encoding,
              com.hubspot.jinjava.interpret.JinjavaInterpreter interpreter)
              throws IOException {
            try (InputStream is = getClass().getResourceAsStream("/templates/" + fullName)) {
              if (is == null) throw new IOException("Template not found: " + fullName);
              return new String(is.readAllBytes(), encoding);
            }
          }
        });
    this.batchSize = batchSize;
  }

  public BaseSynthesizer(int batchSize) {
    this(null, batchSize);
  }

  protected String renderTemplate(String templateName, Map<String, Object> context) {
    String templatePath = "/templates/" + templateName;
    try (InputStream is = getClass().getResourceAsStream(templatePath)) {
      if (is == null) {
        throw new IllegalArgumentException("Template not found: " + templatePath);
      }
      String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      return jinjava.render(template, context);
    } catch (Exception e) {
      throw new RuntimeException("Failed to render template", e);
    }
  }

  protected List<Test> generateSingleTurnBatch(String renderedPrompt) {
    Map<String, Object> rootSchema = SchemaBuilder.buildSingleTurnSchema();
    ChatRequest request = new ChatRequest(renderedPrompt, 0.7, 4000, rootSchema);
    ChatResponse response = modelClient.chat(request);

    List<Test> tests = new ArrayList<>();
    Map<String, Object> props = response.getProperties();

    if (!props.containsKey("tests")) {
      return tests;
    }

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> flatTests = (List<Map<String, Object>>) props.get("tests");

    for (Map<String, Object> flat : flatTests) {
      Prompt promptObj =
          new Prompt(
              null,
              (String) flat.get("prompt_content"),
              "user", // defaulting to user for single turn
              Map.of(
                  "expected_response", flat.get("prompt_expected_response"),
                  "language_code", flat.get("prompt_language_code")));

      tests.add(
          new Test(
              null,
              null,
              (String) flat.get("behavior"),
              (String) flat.get("category"),
              (String) flat.get("topic"),
              TestType.SINGLE_TURN,
              promptObj,
              null,
              null));
    }
    return tests;
  }

  public abstract TestSet generate(int numTests);
}
