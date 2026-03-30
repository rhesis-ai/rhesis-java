package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;

@Builder
public record TestRunSummary(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("created_at") String createdAt,
    @JsonProperty("total_tests") int totalTests,
    @JsonProperty("overall") OverallStats overall,
    @JsonProperty("metrics") Map<String, MetricStats> metrics) {}
