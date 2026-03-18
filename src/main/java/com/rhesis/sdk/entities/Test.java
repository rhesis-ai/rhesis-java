package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhesis.sdk.enums.TestType;
import java.util.List;
import java.util.Map;

public record Test(
    @JsonProperty("id") String id,
    @JsonProperty("test_configuration") TestConfiguration testConfiguration,
    @JsonProperty("behavior") String behavior,
    @JsonProperty("category") String category,
    @JsonProperty("topic") String topic,
    @JsonProperty("test_type") TestType testType,
    @JsonProperty("prompt") Prompt prompt,
    @JsonProperty("metadata") Map<String, Object> metadata,
    @JsonProperty("files") List<String> files) {}
