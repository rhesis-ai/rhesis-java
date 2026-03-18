package ai.rhesis.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.http.InternalHttpClient;
import java.util.List;

public class TestClient {
  private final InternalHttpClient httpClient;

  public TestClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Test get(String id) {
    return httpClient.get("/tests/" + id, Test.class);
  }

  public List<Test> list() {
    return httpClient.get("/tests/", new TypeReference<List<Test>>() {});
  }

  public Test create(Test test) {
    return httpClient.post("/tests/", test, Test.class);
  }

  public void delete(String id) {
    httpClient.delete("/tests/" + id);
  }
}
