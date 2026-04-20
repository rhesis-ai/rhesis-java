package ai.rhesis.sdk.entities;

import ai.rhesis.sdk.enums.TestType;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
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
    // Backend accepts "metadata" on POST but returns "test_metadata" on GET responses
    // (renamed to avoid colliding with SQLAlchemy's reserved Model.metadata). The Python
    // SDK maps "test_metadata" -> "metadata" client-side; we do the same via @JsonAlias.
    @JsonProperty("metadata") @JsonAlias("test_metadata") Map<String, Object> metadata,
    @JsonProperty("files") List<String> files)
    implements BaseEntity<Test> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/tests";
  }
}
