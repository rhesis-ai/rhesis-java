package ai.rhesis.sdk.synthesizers;

import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
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
      context.put("requirements", config.getRequirements());
      context.put("categories", config.getCategories());
      context.put("topics", config.getTopics());
      context.put("additional_context", config.getAdditionalContext());
      context.put("num_tests", currentBatchSize);

      String renderedPrompt = renderTemplate("config_synthesizer.jinja", context);
      generatedTests.addAll(generateSingleTurnBatch(renderedPrompt));
    }

    String name = config.getTestSetName() != null ? config.getTestSetName() : "Synthesized TestSet";
    String description =
        config.getTestSetDescription() != null
            ? config.getTestSetDescription()
            : "Generated with ConfigSynthesizer";
    return new TestSet(null, name, description, TestType.SINGLE_TURN, generatedTests);
  }
}
