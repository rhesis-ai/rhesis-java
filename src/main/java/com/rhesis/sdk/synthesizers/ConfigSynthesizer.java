package com.rhesis.sdk.synthesizers;

import com.rhesis.sdk.entities.Test;
import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.enums.TestType;
import com.rhesis.sdk.models.ChatModelClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigSynthesizer extends BaseSynthesizer {
  private final GenerationConfig config;

  public ConfigSynthesizer(GenerationConfig config, ChatModelClient modelClient, int batchSize) {
    super(modelClient, batchSize);
    this.config = config;
  }

  public ConfigSynthesizer(GenerationConfig config, int batchSize) {
    this(config, null, batchSize);
  }

  public ConfigSynthesizer(GenerationConfig config) {
    this(config, null, 20);
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
        context.put("additional_context", config.getAdditionalContext());
        context.put("num_tests", currentBatchSize);

        String renderedPrompt = renderTemplate("config_synthesizer.jinja", context);
        generatedTests.addAll(generateSingleTurnBatch(renderedPrompt));
    }

    return new TestSet(
        null,
        "Synthesized TestSet",
        "Generated with ConfigSynthesizer",
        TestType.SINGLE_TURN,
        generatedTests);
  }
}
