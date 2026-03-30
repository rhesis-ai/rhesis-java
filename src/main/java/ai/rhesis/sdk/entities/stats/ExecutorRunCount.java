package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ExecutorRunCount(
    @JsonProperty("executor_name") String executorName,
    @JsonProperty("run_count") int runCount) {}
