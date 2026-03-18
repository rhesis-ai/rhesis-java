package ai.rhesis.sdk.unit.entities;

import static org.assertj.core.api.Assertions.assertThat;

import ai.rhesis.sdk.entities.*;
import ai.rhesis.sdk.models.ChatRequest;
import ai.rhesis.sdk.synthesizers.SynthesizerConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
    ChatRequest validRequest = new ChatRequest("Hello", 0.7, 100, null);
    Set<ConstraintViolation<ChatRequest>> violations = validator.validate(validRequest);
    assertThat(violations).isEmpty();

    ChatRequest invalidRequest = new ChatRequest("", null, null, null);
    violations = validator.validate(invalidRequest);
    assertThat(violations).hasSize(1);
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
