package com.rhesis.sdk.exceptions;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

public class RhesisValidationException extends RuntimeException {
  private final Set<? extends ConstraintViolation<?>> violations;

  public RhesisValidationException(Set<? extends ConstraintViolation<?>> violations) {
    super("Validation failed: " + formatViolations(violations));
    this.violations = violations;
  }

  public Set<? extends ConstraintViolation<?>> getViolations() {
    return violations;
  }

  private static String formatViolations(Set<? extends ConstraintViolation<?>> violations) {
    return violations.stream()
        .map(v -> v.getPropertyPath() + " " + v.getMessage())
        .collect(Collectors.joining(", "));
  }
}
