package ai.rhesis.sdk;

import io.github.cdimascio.dotenv.Dotenv;

public class RhesisClientBuilder {
  private String apiKey;
  private String baseUrl = "https://api.rhesis.ai/";

  public RhesisClientBuilder apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  public RhesisClientBuilder baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  public RhesisClient build() {
    if (apiKey == null || apiKey.trim().isEmpty()) {
      apiKey = System.getenv("RHESIS_API_KEY");

      // Fallback to .env file if system env is not set
      if (apiKey == null || apiKey.trim().isEmpty()) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        apiKey = dotenv.get("RHESIS_API_KEY");
      }

      if (apiKey == null || apiKey.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "API Key must be provided either via builder, RHESIS_API_KEY environment variable, or in a .env file");
      }
    }

    if (baseUrl == null || baseUrl.equals("https://api.rhesis.ai/")) {
      String envBaseUrl = System.getenv("RHESIS_BASE_URL");

      if (envBaseUrl == null || envBaseUrl.trim().isEmpty()) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        envBaseUrl = dotenv.get("RHESIS_BASE_URL");
      }

      if (envBaseUrl != null && !envBaseUrl.trim().isEmpty()) {
        baseUrl = envBaseUrl;
      }
    }

    return new RhesisClient(baseUrl, apiKey);
  }
}
