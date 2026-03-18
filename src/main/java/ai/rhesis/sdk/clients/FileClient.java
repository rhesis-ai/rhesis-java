package ai.rhesis.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.http.InternalHttpClient;
import java.util.List;

public class FileClient {
  private final InternalHttpClient httpClient;

  public FileClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<File> list() {
    return httpClient.get("/files/", new TypeReference<List<File>>() {});
  }

  public File get(String id) {
    return httpClient.get("/files/" + id, File.class);
  }

  public byte[] download(String id) {
    return httpClient.get("/files/" + id + "/content", byte[].class);
  }
}
