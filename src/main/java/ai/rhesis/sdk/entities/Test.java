package ai.rhesis.sdk.entities;

import ai.rhesis.sdk.enums.TestType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("files") List<String> files)
    implements BaseEntity<Test> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/tests";
  }
}
