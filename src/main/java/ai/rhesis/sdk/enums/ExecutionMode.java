package ai.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExecutionMode {
  PARALLEL("Parallel"),
  SEQUENTIAL("Sequential");

  private final String value;

  ExecutionMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ExecutionMode fromValue(String value) {
    if (value == null) {
      return PARALLEL;
    }
    String normalized = value.strip().toLowerCase();
    for (ExecutionMode mode : values()) {
      if (mode.value.toLowerCase().equals(normalized)) {
        return mode;
      }
    }
    throw new IllegalArgumentException(
        "Invalid execution mode: '"
            + value
            + "'. Use 'parallel', 'sequential', or ExecutionMode.PARALLEL / ExecutionMode.SEQUENTIAL");
  }
}
