package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Endpoint;
import ai.rhesis.sdk.entities.Project;
import ai.rhesis.sdk.enums.ConnectionType;
import java.util.Map;

public class CreateEndpointExample {
  public static void main(String[] args) {
    // Initialize the global client
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();
    RhesisClient.setDefault(client);

    // Endpoints must be connected to a Project.
    // You can find a project by its name instead of fetching the list.
    Project project;
    try {
      project = client.projects().findByName("Example Project (Insurance Chatbot)");
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      System.exit(1);
      return;
    }

    System.out.println("Using Project ID: " + project.id());

    // Build the endpoint object
    Endpoint newEndpoint =
        Endpoint.builder()
            .name("My Example Endpoint")
            .description("A sample endpoint created via Java SDK")
            .connectionType(ConnectionType.REST)
            .url("https://api.example.com")
            .projectId(project.id())
            .method("POST")
            .endpointPath("/v1/chat")
            .requestHeaders(
                Map.of(
                    "Authorization", "Bearer {{ auth_token }}", "Content-Type", "application/json"))
            .requestMapping(Map.of("message", "{{ input }}"))
            .responseMapping(Map.of("output", "$.response.text"))
            .authToken("my-secret-token")
            .build();

    // Create the endpoint using the Active Record style!
    Endpoint created = newEndpoint.push();
    System.out.println("Created Endpoint ID: " + created.id());
  }
}
