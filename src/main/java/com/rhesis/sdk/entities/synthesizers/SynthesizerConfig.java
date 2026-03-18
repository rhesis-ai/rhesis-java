package com.rhesis.sdk.entities.synthesizers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SynthesizerConfig(
    @NotBlank @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @NotBlank @JsonProperty("base_model") String baseModel) {}
