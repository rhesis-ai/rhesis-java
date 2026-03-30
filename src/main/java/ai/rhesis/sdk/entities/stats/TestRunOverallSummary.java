package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TestRunOverallSummary(
    @JsonProperty("total_runs") int totalRuns,
    @JsonProperty("unique_test_sets") int uniqueTestSets,
    @JsonProperty("unique_executors") int uniqueExecutors,
    @JsonProperty("most_common_status") String mostCommonStatus,
    @JsonProperty("pass_rate") double passRate) {}
