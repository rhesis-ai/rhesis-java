package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class InsightsResponse {
  @JsonProperty("entity")
  private String entity;

  @JsonProperty("dimensions")
  private List<String> dimensions;

  @JsonProperty("measures")
  private List<String> measures;

  @JsonProperty("rows")
  private List<Map<String, Object>> rows;

  public String entity() {
    return entity;
  }

  public List<String> dimensions() {
    return dimensions;
  }

  public List<String> measures() {
    return measures;
  }

  public List<Map<String, Object>> rows() {
    return rows;
  }
}
