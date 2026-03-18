package com.rhesis.sdk.synthesizers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GenerationConfig(
    @JsonProperty("generation_prompt") String generationPrompt,
    @JsonProperty("behaviors") List<String> behaviors,
    @JsonProperty("categories") List<String> categories,
    @JsonProperty("topics") List<String> topics,
    @JsonProperty("additional_context") String additionalContext) {}
