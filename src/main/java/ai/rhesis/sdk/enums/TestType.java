package ai.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TestType {
  MULTI_TURN("Multi-Turn"),
  SINGLE_TURN("Single-Turn");

  private final String value;

  TestType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
