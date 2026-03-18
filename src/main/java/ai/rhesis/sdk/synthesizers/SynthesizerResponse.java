package ai.rhesis.sdk.synthesizers;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SynthesizerResponse(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("base_model") String baseModel,
    @JsonProperty("status") String status) {}
