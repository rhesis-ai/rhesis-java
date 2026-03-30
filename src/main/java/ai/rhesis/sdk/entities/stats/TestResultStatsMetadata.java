package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record TestResultStatsMetadata(
    @JsonProperty("generated_at") String generatedAt,
    @JsonProperty("organization_id") String organizationId,
    @JsonProperty("test_run_id") String testRunId,
    @JsonProperty("period") String period,
    @JsonProperty("start_date") String startDate,
    @JsonProperty("end_date") String endDate,
    @JsonProperty("total_test_runs") int totalTestRuns,
    @JsonProperty("total_test_results") int totalTestResults,
    @JsonProperty("mode") String mode,
    @JsonProperty("available_metrics") List<String> availableMetrics,
    @JsonProperty("available_behaviors") List<String> availableBehaviors,
    @JsonProperty("available_categories") List<String> availableCategories,
    @JsonProperty("available_topics") List<String> availableTopics) {}
