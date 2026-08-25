package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class InsightsIdsResponse {
  @JsonProperty("entity")
  private String entity;

  @JsonProperty("ids")
  private List<String> ids;

  public String entity() {
    return entity;
  }

  public List<String> ids() {
    return ids;
  }
}
