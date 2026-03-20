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

  @com.fasterxml.jackson.annotation.JsonCreator
  public static TestType fromValue(Object value) {
    if (value == null) {
      return null;
    }
    String strValue;
    if (value instanceof java.util.Map) {
      @SuppressWarnings("unchecked")
      java.util.Map<String, Object> map = (java.util.Map<String, Object>) value;
      Object typeValue = map.get("type_value");
      if (typeValue == null) {
        return null;
      }
      strValue = typeValue.toString();
    } else {
      strValue = value.toString();
    }
    for (TestType type : values()) {
      if (type.value.equals(strValue)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown TestType value: " + value);
  }
}
