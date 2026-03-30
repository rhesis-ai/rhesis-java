package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TestSetRunCount(
    @JsonProperty("test_set_name") String testSetName, @JsonProperty("run_count") int runCount) {}
