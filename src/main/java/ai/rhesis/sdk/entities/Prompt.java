package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Builder;

@Builder
public record Prompt(
    @JsonProperty("id") String id,
    @NotBlank @JsonProperty("content") String content,
    @JsonProperty("role") String role,
    @JsonProperty("metadata") Map<String, Object> metadata)
    implements BaseEntity<Prompt> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/prompts";
  }
}
