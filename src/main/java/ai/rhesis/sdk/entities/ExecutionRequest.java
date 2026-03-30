package ai.rhesis.sdk.entities;

import ai.rhesis.sdk.enums.ExecutionMode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExecutionRequest(
    @JsonProperty("execution_options") ExecutionOptions executionOptions,
    @JsonProperty("metrics") List<Map<String, Object>> metrics,
    @JsonProperty("reference_test_run_id") String referenceTestRunId) {

  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record ExecutionOptions(
      @JsonProperty("execution_mode") ExecutionMode executionMode) {}

  public static ExecutionRequest forExecution(
      ExecutionMode mode, List<Map<String, Object>> metrics) {
    return ExecutionRequest.builder()
        .executionOptions(ExecutionOptions.builder().executionMode(mode).build())
        .metrics(metrics)
        .build();
  }

  public static ExecutionRequest forRescore(
      ExecutionMode mode, List<Map<String, Object>> metrics, String referenceTestRunId) {
    return ExecutionRequest.builder()
        .executionOptions(ExecutionOptions.builder().executionMode(mode).build())
        .metrics(metrics)
        .referenceTestRunId(referenceTestRunId)
        .build();
  }
}
