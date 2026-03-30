package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record TestRunStats(
    @JsonProperty("status_distribution") List<StatusDistribution> statusDistribution,
    @JsonProperty("result_distribution") ResultDistribution resultDistribution,
    @JsonProperty("most_run_test_sets") List<TestSetRunCount> mostRunTestSets,
    @JsonProperty("top_executors") List<ExecutorRunCount> topExecutors,
    @JsonProperty("timeline") List<TestRunTimelineData> timeline,
    @JsonProperty("overall_summary") TestRunOverallSummary overallSummary,
    @JsonProperty("metadata") TestRunStatsMetadata metadata) {}
