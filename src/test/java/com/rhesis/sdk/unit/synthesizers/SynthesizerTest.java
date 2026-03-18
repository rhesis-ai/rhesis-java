package com.rhesis.sdk.unit.synthesizers;

import com.rhesis.sdk.synthesizers.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rhesis.sdk.entities.TestSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class SynthesizerTest {

  @Test
  void testSynthesizerRender() {
    GenerationConfig config = GenerationConfig.builder()
        .generationPrompt("My Prompt")
        .behaviors(List.of("B1", "B2"))
        .categories(List.of("C1"))
        .build();
    Synthesizer synth = new Synthesizer(config, 20);
    String output = synth.getRenderedPrompt();
    assertThat(output).contains("My Prompt");
    assertThat(output).contains("[B1, B2]");
    assertThat(output).contains("[C1]");
    assertThat(output).contains("Analyze the generation prompt and derive relevant topics");

    TestSet ts = synth.generate(5);
    assertThat(ts.testSetType()).isEqualTo(com.rhesis.sdk.enums.TestType.SINGLE_TURN);
  }

  @Test
  void testMultiTurnSynthesizerRender() {
    GenerationConfig config = GenerationConfig.builder()
        .generationPrompt("My Prompt")
        .topics(List.of("T1", "T2"))
        .build();
    MultiTurnSynthesizer synth = new MultiTurnSynthesizer(config, 20);
    String output = synth.getRenderedPrompt();
    assertThat(output).contains("My Prompt");
    assertThat(output).contains("T1");
    assertThat(output).contains("T2");
    assertThat(output).contains("Use the following default behaviors:");
  }
}
