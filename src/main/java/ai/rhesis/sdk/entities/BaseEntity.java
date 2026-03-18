package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ai.rhesis.sdk.RhesisClient;
import java.util.Map;

public interface BaseEntity<T extends BaseEntity<T>> {
  ObjectMapper MAPPER =
      new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

  String id();

  String getEndpointPath();

  default Map<String, Object> toMap() {
    return MAPPER.convertValue(this, new TypeReference<Map<String, Object>>() {});
  }

  default String toJson() {
    try {
      return MAPPER.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize to JSON", e);
    }
  }

  default void delete() {
    if (id() == null) throw new IllegalStateException("Cannot delete entity without ID");
    String path = getEndpointPath() + "/" + id();
    RhesisClient.getDefault().getHttpClient().delete(path);
  }

  @SuppressWarnings("unchecked")
  default T pull() {
    if (id() == null) throw new IllegalStateException("Cannot pull entity without ID");
    String path = getEndpointPath() + "/" + id();
    return (T) RhesisClient.getDefault().getHttpClient().get(path, getClass());
  }

  @SuppressWarnings("unchecked")
  default T push() {
    if (id() == null) {
      return (T)
          RhesisClient.getDefault().getHttpClient().post(getEndpointPath() + "/", this, getClass());
    } else {
      // Update using PUT
      return (T)
          RhesisClient.getDefault()
              .getHttpClient()
              .put(getEndpointPath() + "/" + id(), this, getClass());
    }
  }
}
