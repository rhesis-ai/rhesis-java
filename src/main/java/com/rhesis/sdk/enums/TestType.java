package com.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TestType {
  MULTI_TURN("MULTI_TURN"),
  SINGLE_TURN("SINGLE_TURN");

  private final String value;

  TestType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
