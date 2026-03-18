package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.Project;
import ai.rhesis.sdk.http.InternalHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProjectClient {
  private final InternalHttpClient httpClient;

  public ProjectClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<Project> list() {
    return httpClient.get("/projects/", new TypeReference<List<Project>>() {});
  }

  public Project findByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name must be provided");
    }

    String encodedName =
        URLEncoder.encode(name.toLowerCase(java.util.Locale.ROOT), StandardCharsets.UTF_8)
            .replace("+", "%20");
    String filter = "?$filter=tolower(name)%20eq%20'" + encodedName + "'";

    List<Project> projects =
        httpClient.get("/projects/" + filter, new TypeReference<List<Project>>() {});

    if (projects == null || projects.isEmpty()) {
      throw new IllegalArgumentException("No entity found with name '" + name + "'");
    }

    if (projects.size() > 1) {
      List<String> ids = projects.stream().map(Project::id).toList();
      throw new IllegalArgumentException(
          "More than one entity found with name '"
              + name
              + "'. Entity names must be unique. Please use the entity id instead. Matching entity IDs: "
              + String.join(", ", ids));
    }

    return projects.get(0);
  }
}
