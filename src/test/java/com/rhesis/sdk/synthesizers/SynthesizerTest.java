package com.rhesis.sdk.synthesizers;

import static org.assertj.core.api.Assertions.assertThat;

import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.enums.TestType;
import java.util.List;
import org.junit.jupiter.api.Test;

class SynthesizerTest {

  @Test
  void testSynthesizerRender() {
    Synthesizer synth = new Synthesizer("My Prompt", List.of("B1", "B2"), List.of("C1"), null, 20);
    String output = synth.getRenderedPrompt();
    assertThat(output).contains("Prompt: My Prompt");
    assertThat(output).contains("Behaviors: B1, B2");
    assertThat(output).contains("Categories: C1");
    assertThat(output).doesNotContain("Topics:");

    TestSet ts = synth.generate(5);
    assertThat(ts.testSetType()).isEqualTo(TestType.SINGLE_TURN);
  }

  @Test
  void testMultiTurnSynthesizerRender() {
    MultiTurnSynthesizer synth =
        new MultiTurnSynthesizer("My Prompt", null, null, List.of("T1", "T2"), 20);
    String output = synth.getRenderedPrompt();
    assertThat(output).contains("Prompt: My Prompt");
    assertThat(output).contains("Topics: T1, T2");
    assertThat(output).doesNotContain("Behaviors:");

    TestSet ts = synth.generate(5);
    assertThat(ts.testSetType()).isEqualTo(TestType.MULTI_TURN);
  }
}
