package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record File(
    @JsonProperty("id") String id,
    @JsonProperty("filename") String filename,
    @JsonProperty("content_type") String contentType,
    @JsonProperty("size_bytes") Integer sizeBytes,
    @JsonProperty("description") String description,
    @JsonProperty("entity_id") String entityId,
    @JsonProperty("entity_type") String entityType,
    @JsonProperty("position") Integer position)
    implements BaseEntity<File> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/files";
  }
}
