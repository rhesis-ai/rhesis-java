package ai.rhesis.sdk.synthesizers;

import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextSynthesizer extends BaseSynthesizer {
  private final GenerationConfig config;

  public ContextSynthesizer(GenerationConfig config, ChatModelClient modelClient, int batchSize) {
    super(modelClient, batchSize);
    this.config = config;
  }

  public ContextSynthesizer(GenerationConfig config, int batchSize) {
    this(config, null, batchSize);
  }

  public ContextSynthesizer(GenerationConfig config) {
    this(config, null, 20);
  }

  // Backwards compatibility constructor
  public ContextSynthesizer(String prompt, String contextData, int batchSize) {
    this(
        GenerationConfig.builder().generationPrompt(prompt).additionalContext(contextData).build(),
        null,
        batchSize);
  }

  public ContextSynthesizer(String prompt, String contextData) {
    this(prompt, contextData, 20);
  }

  @Override
  public TestSet generate(int numTests) {
    if (config.getAdditionalContext() == null || config.getAdditionalContext().isEmpty()) {
      throw new IllegalArgumentException("Context cannot be empty");
    }

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
      // Map additionalContext to context to match the jinja template
      context.put("context", config.getAdditionalContext());
      context.put("behaviors", config.getBehaviors());
      context.put("categories", config.getCategories());
      context.put("topics", config.getTopics());
      context.put("num_tests", currentBatchSize);

      String renderedPrompt = renderTemplate("context_synthesizer.jinja", context);
      generatedTests.addAll(generateSingleTurnBatch(renderedPrompt));
    }

    return new TestSet(
        null,
        "Synthesized TestSet",
        "Generated with ContextSynthesizer",
        TestType.SINGLE_TURN,
        generatedTests);
  }
}
