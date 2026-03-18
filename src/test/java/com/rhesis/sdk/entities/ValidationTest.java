package com.rhesis.sdk.entities;

import static org.assertj.core.api.Assertions.assertThat;

import com.rhesis.sdk.models.ChatRequest;
import com.rhesis.sdk.synthesizers.SynthesizerConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void testChatRequestValidation() {
    ChatRequest validRequest =
        new ChatRequest(
            "model-id",
            Collections.singletonList(new ChatRequest.Message("user", "Hello")),
            0.7,
            100);
    Set<ConstraintViolation<ChatRequest>> violations = validator.validate(validRequest);
    assertThat(violations).isEmpty();

    ChatRequest invalidRequest = new ChatRequest("", Collections.emptyList(), null, null);
    violations = validator.validate(invalidRequest);
    assertThat(violations).hasSize(2);
  }

  @Test
  void testSynthesizerConfigValidation() {
    SynthesizerConfig validConfig = new SynthesizerConfig("My Synth", "Description", "base-model");
    Set<ConstraintViolation<SynthesizerConfig>> violations = validator.validate(validConfig);
    assertThat(violations).isEmpty();

    SynthesizerConfig invalidConfig = new SynthesizerConfig("", null, "");
    violations = validator.validate(invalidConfig);
    assertThat(violations).hasSize(2);
  }
}
