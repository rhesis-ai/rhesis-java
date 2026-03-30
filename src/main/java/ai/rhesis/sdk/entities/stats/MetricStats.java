package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record MetricStats(
    @JsonProperty("total") int total,
    @JsonProperty("passed") int passed,
    @JsonProperty("failed") int failed,
    @JsonProperty("pass_rate") double passRate) {}
