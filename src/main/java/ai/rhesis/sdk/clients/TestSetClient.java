package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.http.InternalHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class TestSetClient {
  private final InternalHttpClient httpClient;

  public TestSetClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public TestSet get(String id) {
    return httpClient.get("/test_sets/" + id, TestSet.class);
  }

  public List<TestSet> list() {
    return httpClient.get("/test_sets/", new TypeReference<List<TestSet>>() {});
  }

  public TestSet create(TestSet testSet) {
    return httpClient.post("/test_sets/bulk", testSet, TestSet.class);
  }

  public void delete(String id) {
    httpClient.delete("/test_sets/" + id);
  }
}
