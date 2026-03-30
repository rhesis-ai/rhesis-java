package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;

@Builder
public record TimelineData(
    @JsonProperty("date") String date,
    @JsonProperty("overall") OverallStats overall,
    @JsonProperty("metrics") Map<String, MetricStats> metrics) {}
