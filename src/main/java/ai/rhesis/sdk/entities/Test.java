package ai.rhesis.sdk.entities;

import ai.rhesis.sdk.enums.TestType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record Test(
    @JsonProperty("id") String id,
    @JsonProperty("test_configuration") TestConfiguration testConfiguration,
    @JsonProperty("behavior")
        @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = NameStringDeserializer.class)
        String behavior,
    @JsonProperty("category")
        @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = NameStringDeserializer.class)
        String category,
    @JsonProperty("topic")
        @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
            using = NameStringDeserializer.class)
        String topic,
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
