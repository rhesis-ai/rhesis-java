package ai.rhesis.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record ChatRequest(
    @NotBlank @JsonProperty("prompt") String prompt,
    @JsonProperty("temperature") Double temperature,
    @JsonProperty("max_tokens") Integer maxTokens,
    @JsonProperty("schema") Map<String, Object> schema) {}
