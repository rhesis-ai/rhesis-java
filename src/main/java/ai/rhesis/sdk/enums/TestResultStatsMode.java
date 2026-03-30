package ai.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TestResultStatsMode {
  ALL("all"),
  SUMMARY("summary"),
  METRICS("metrics"),
  BEHAVIOR("behavior"),
  CATEGORY("category"),
  TOPIC("topic"),
  OVERALL("overall"),
  TIMELINE("timeline"),
  TEST_RUNS("test_runs");

  private final String value;

  TestResultStatsMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static TestResultStatsMode fromValue(String value) {
    for (TestResultStatsMode mode : values()) {
      if (mode.value.equalsIgnoreCase(value)) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Invalid TestResultStatsMode: " + value);
  }
}
