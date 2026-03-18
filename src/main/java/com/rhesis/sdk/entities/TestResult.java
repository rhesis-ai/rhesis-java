package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record TestResult(
    @JsonProperty("id") String id,
    @JsonProperty("test_configuration_id") String testConfigurationId,
    @JsonProperty("test_run_id") String testRunId,
    @JsonProperty("prompt_id") String promptId,
    @JsonProperty("test_id") String testId,
    @JsonProperty("status_id") String statusId,
    @JsonProperty("status") Status status,
    @JsonProperty("test_output") Map<String, Object> testOutput,
    @JsonProperty("test_metrics") Map<String, Object> testMetrics,
    @JsonProperty("test_reviews") Map<String, Object> testReviews)
    implements BaseEntity<TestResult> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/test_results";
  }
}
