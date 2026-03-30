package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import lombok.Builder;

@Builder
public record TestRun(
    @JsonProperty("id") String id,
    @JsonProperty("test_configuration_id") String testConfigurationId,
    @JsonProperty("name") String name,
    @JsonProperty("user_id") String userId,
    @JsonProperty("organization_id") String organizationId,
    @JsonProperty("status") @JsonDeserialize(using = NameStringDeserializer.class) String status,
    @JsonProperty("attributes") Map<String, Object> attributes,
    @JsonProperty("owner_id") String ownerId,
    @JsonProperty("assignee_id") String assigneeId,
    @JsonProperty("created_at") String createdAt,
    @JsonProperty("nano_id") String nanoId,
    @JsonProperty("test_count") Integer testCount,
    @JsonProperty("pass_rate") Double passRate)
    implements BaseEntity<TestRun> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/test_runs";
  }
}
