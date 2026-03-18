package ai.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RunStatus {
  PROGRESS("Progress"),
  COMPLETED("Completed"),
  PARTIAL("Partial"),
  FAILED("Failed");

  private final String value;

  RunStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static RunStatus fromValue(String value) {
    for (RunStatus status : values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    return null; // Handle gracefully if API returns something unexpected
  }
}
