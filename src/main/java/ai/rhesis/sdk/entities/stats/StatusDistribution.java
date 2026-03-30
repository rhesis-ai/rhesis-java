package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record StatusDistribution(
    @JsonProperty("status") String status,
    @JsonProperty("count") int count,
    @JsonProperty("percentage") double percentage) {}
