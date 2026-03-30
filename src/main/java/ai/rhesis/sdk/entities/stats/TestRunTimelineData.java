package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;

@Builder
public record TestRunTimelineData(
    @JsonProperty("date") String date,
    @JsonProperty("total_runs") int totalRuns,
    @JsonProperty("result_breakdown") Map<String, Integer> resultBreakdown) {}
