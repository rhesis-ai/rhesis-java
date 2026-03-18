package com.rhesis.sdk.integration;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.entities.Project;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseIntegrationTest {

  protected static RhesisClient client;
  protected static String defaultProjectId;

  @BeforeAll
  static void setUp() {
    String apiKey = null;
    String baseUrl = null;

    try {
      Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
      apiKey = dotenv.get("RHESIS_API_KEY");
      baseUrl = dotenv.get("RHESIS_BASE_URL");
    } catch (DotenvException e) {
      // Ignored
    }

    if (apiKey == null) {
      apiKey = System.getenv("RHESIS_API_KEY");
    }
    if (baseUrl == null) {
      baseUrl = System.getenv("RHESIS_BASE_URL");
    }

    assumeTrue(
        apiKey != null && !apiKey.isEmpty(),
        "RHESIS_API_KEY is not set. Skipping integration tests.");

    client =
        RhesisClient.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl != null ? baseUrl : "https://api.rhesis.ai")
            .build();

    try {
      List<Project> projects = client.projects().list();
      if (!projects.isEmpty()) {
        defaultProjectId = projects.get(0).id();
      }
    } catch (Exception e) {
      System.err.println("Could not fetch projects: " + e.getMessage());
    }
  }
}
