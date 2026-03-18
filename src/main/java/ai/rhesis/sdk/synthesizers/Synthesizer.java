package ai.rhesis.sdk.synthesizers;

import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Synthesizer extends BaseSynthesizer {
  private final GenerationConfig config;

  public Synthesizer(GenerationConfig config, ChatModelClient modelClient, int batchSize) {
    super(modelClient, batchSize);
    this.config = config;
  }

  public Synthesizer(GenerationConfig config, int batchSize) {
    this(config, null, batchSize);
  }

  public Synthesizer(GenerationConfig config) {
    this(config, null, 20);
  }

  // Backwards compatibility constructor
  public Synthesizer(String prompt) {
    this(GenerationConfig.builder().generationPrompt(prompt).build(), null, 20);
  }

  @Override
  public TestSet generate(int numTests) {
    List<Test> generatedTests = new ArrayList<>();
    int numBatches = numTests / batchSize;
    int currentBatchSize = batchSize;

    if (numBatches == 0) {
      numBatches = 1;
      currentBatchSize = numTests;
    }

    for (int i = 0; i < numBatches; i++) {
      Map<String, Object> context = new HashMap<>();
      context.put("generation_prompt", config.getGenerationPrompt());
      context.put("behaviors", config.getBehaviors());
      context.put("categories", config.getCategories());
      context.put("topics", config.getTopics());
      context.put("num_tests", currentBatchSize);

      String renderedPrompt = renderTemplate("synthesizer.jinja", context);
      generatedTests.addAll(generateSingleTurnBatch(renderedPrompt));
    }

    return new TestSet(
        null,
        "Synthesized TestSet",
        "Generated with Synthesizer based on prompt: " + config.getGenerationPrompt(),
        TestType.SINGLE_TURN,
        generatedTests);
  }

  public String getRenderedPrompt() {
    Map<String, Object> context = new HashMap<>();
    context.put("generation_prompt", config.getGenerationPrompt());
    context.put("behaviors", config.getBehaviors());
    context.put("categories", config.getCategories());
    context.put("topics", config.getTopics());
    return renderTemplate("synthesizer.jinja", context);
  }
}
