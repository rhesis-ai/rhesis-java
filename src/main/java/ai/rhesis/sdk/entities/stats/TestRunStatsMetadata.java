package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record TestRunStatsMetadata(
    @JsonProperty("generated_at") String generatedAt,
    @JsonProperty("organization_id") String organizationId,
    @JsonProperty("period") String period,
    @JsonProperty("start_date") String startDate,
    @JsonProperty("end_date") String endDate,
    @JsonProperty("total_test_runs") int totalTestRuns,
    @JsonProperty("mode") String mode,
    @JsonProperty("available_statuses") List<String> availableStatuses,
    @JsonProperty("available_test_sets") List<String> availableTestSets,
    @JsonProperty("available_executors") List<String> availableExecutors) {}
