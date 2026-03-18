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
        new Endpoint(
            null,
            "Integration Test Endpoint",
            "Created by Java SDK Integration Tests",
            ConnectionType.REST,
            "https://httpbin.org/post",
            defaultProjectId,
            "POST",
            null,
            Map.of("Content-Type", "application/json", "Authorization", "Bearer {{ auth_token }}"),
            null,
            Map.of("message", "{{ input }}"),
            Map.of("output", "$.data"),
            "fake-token");

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
        new Endpoint(
            created.id(),
            "Updated Integration Test Endpoint",
            "Updated by Java SDK Integration Tests",
            created.connectionType(),
            created.url(),
            created.projectId(),
            created.method(),
            created.endpointPath(),
            created.requestHeaders(),
            created.queryParams(),
            created.requestMapping(),
            created.responseMapping(),
            "new-fake-token");

    Endpoint updated = updatedEndpoint.push();
    assertThat(updated).isNotNull();
    assertThat(updated.name()).isEqualTo("Updated Integration Test Endpoint");

    Endpoint fetchedUpdated = updated.pull();
    assertThat(fetchedUpdated.name()).isEqualTo("Updated Integration Test Endpoint");

    created.delete();
  }
}
