package ai.rhesis.sdk.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Endpoint;
import ai.rhesis.sdk.enums.ConnectionType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EndpointIntegrationTest extends BaseIntegrationTest {

  @BeforeAll
  static void setupGlobalClient() {
    RhesisClient.setDefault(client);
  }

  @Test
  void testEndpointLifecycle() {
    assumeTrue(defaultProjectId != null, "Project ID is required to create an endpoint");
    Endpoint newEndpoint =
        Endpoint.builder()
            .name("Integration Test Endpoint")
            .description("Created by Java SDK Integration Tests")
            .connectionType(ConnectionType.REST)
            .url("https://httpbin.org/post")
            .projectId(defaultProjectId)
            .method("POST")
            .requestHeaders(
                Map.of(
                    "Content-Type", "application/json", "Authorization", "Bearer {{ auth_token }}"))
            .requestMapping(Map.of("message", "{{ input }}"))
            .responseMapping(Map.of("output", "$.data"))
            .authToken("fake-token")
            .build();

    Endpoint created = newEndpoint.push();
    assertThat(created).isNotNull();
    assertThat(created.id()).isNotNull();
    assertThat(created.name()).isEqualTo("Integration Test Endpoint");

    Endpoint fetched = created.pull();
    assertThat(fetched).isNotNull();
    assertThat(fetched.id()).isEqualTo(created.id());

    List<Endpoint> endpoints = client.endpoints().list();
    assertThat(endpoints).isNotEmpty();
    boolean found = endpoints.stream().anyMatch(e -> e.id().equals(created.id()));
    assertThat(found).isTrue();

    // Test Update
    Endpoint updatedEndpoint =
        Endpoint.builder()
            .id(created.id())
            .name("Updated Integration Test Endpoint")
            .description("Updated by Java SDK Integration Tests")
            .connectionType(created.connectionType())
            .url(created.url())
            .projectId(created.projectId())
            .method(created.method())
            .endpointPath(created.endpointPath())
            .requestHeaders(created.requestHeaders())
            .queryParams(created.queryParams())
            .requestMapping(created.requestMapping())
            .responseMapping(created.responseMapping())
            .authToken("new-fake-token")
            .build();

    Endpoint updated = updatedEndpoint.push();
    assertThat(updated).isNotNull();
    assertThat(updated.name()).isEqualTo("Updated Integration Test Endpoint");

    Endpoint fetchedUpdated = updated.pull();
    assertThat(fetchedUpdated.name()).isEqualTo("Updated Integration Test Endpoint");

    created.delete();
  }
}
