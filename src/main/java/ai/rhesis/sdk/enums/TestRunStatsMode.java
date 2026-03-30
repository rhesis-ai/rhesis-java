package ai.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TestRunStatsMode {
  ALL("all"),
  SUMMARY("summary"),
  STATUS("status"),
  RESULTS("results"),
  TEST_SETS("test_sets"),
  EXECUTORS("executors"),
  TIMELINE("timeline");

  private final String value;

  TestRunStatsMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static TestRunStatsMode fromValue(String value) {
    for (TestRunStatsMode mode : values()) {
      if (mode.value.equalsIgnoreCase(value)) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Invalid TestRunStatsMode: " + value);
  }
}
