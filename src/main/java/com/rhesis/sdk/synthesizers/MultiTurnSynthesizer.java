package com.rhesis.sdk.synthesizers;

import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.enums.TestType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiTurnSynthesizer extends BaseSynthesizer {
  private final String prompt;
  private final List<String> behaviors;
  private final List<String> categories;
  private final List<String> topics;

  public MultiTurnSynthesizer(
      String prompt,
      List<String> behaviors,
      List<String> categories,
      List<String> topics,
      int batchSize) {
    super(batchSize);
    this.prompt = prompt;
    this.behaviors = behaviors != null ? behaviors : new ArrayList<>();
    this.categories = categories != null ? categories : new ArrayList<>();
    this.topics = topics != null ? topics : new ArrayList<>();
  }

  public MultiTurnSynthesizer(String prompt) {
    this(prompt, null, null, null, 20);
  }

  @Override
  public TestSet generate(int numTests) {
    Map<String, Object> context = new HashMap<>();
    context.put("prompt", prompt);
    context.put("behaviors", behaviors);
    context.put("categories", categories);
    context.put("topics", topics);

    String renderedPrompt = renderTemplate("multi_turn_synthesizer.jinja", context);
    // Here we would normally call the LLM model to generate tests using the rendered prompt.

    return new TestSet(
        null,
        "Synthesized TestSet (Multi-Turn)",
        "Generated with MultiTurnSynthesizer",
        TestType.MULTI_TURN,
        new ArrayList<>());
  }

  public String getRenderedPrompt() {
    Map<String, Object> context = new HashMap<>();
    context.put("prompt", prompt);
    context.put("behaviors", behaviors);
    context.put("categories", categories);
    context.put("topics", topics);
    return renderTemplate("multi_turn_synthesizer.jinja", context);
  }
}
