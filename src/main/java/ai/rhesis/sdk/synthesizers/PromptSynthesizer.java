package ai.rhesis.sdk.synthesizers;

import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromptSynthesizer extends BaseSynthesizer {
  private final GenerationConfig config;

  public PromptSynthesizer(GenerationConfig config, ChatModelClient modelClient, int batchSize) {
    super(modelClient, batchSize);
    this.config = config;
  }

  public PromptSynthesizer(GenerationConfig config, int batchSize) {
    this(config, null, batchSize);
  }

  public PromptSynthesizer(GenerationConfig config) {
    this(config, null, 20);
  }

  // Backwards compatibility constructor
  public PromptSynthesizer(String prompt, int batchSize) {
    this(GenerationConfig.builder().generationPrompt(prompt).build(), null, batchSize);
  }

  public PromptSynthesizer(String prompt) {
    this(prompt, 20);
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

      String renderedPrompt = renderTemplate("prompt_synthesizer.jinja", context);
      generatedTests.addAll(generateSingleTurnBatch(renderedPrompt));
    }

    return new TestSet(
        null,
        "Synthesized TestSet",
        "Generated with PromptSynthesizer based on prompt: " + config.getGenerationPrompt(),
        TestType.SINGLE_TURN,
        generatedTests);
  }
}
