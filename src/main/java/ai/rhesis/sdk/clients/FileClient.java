package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.http.InternalHttpClient;
import ai.rhesis.sdk.http.MultipartBuilder;
import ai.rhesis.sdk.models.FileUpload;
import com.fasterxml.jackson.core.type.TypeReference;
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

  public List<File> upload(List<FileUpload> files, String entityId, String entityType) {
    MultipartBuilder multipartBuilder = new MultipartBuilder();
    for (FileUpload file : files) {
      multipartBuilder.addPart(
          "files", file.getFilename(), file.getContentType(), file.getContent());
    }

    String path = "/files/?entity_id=" + entityId + "&entity_type=" + entityType;
    return httpClient.postMultipart(path, multipartBuilder, new TypeReference<List<File>>() {});
  }

  public void delete(String id) {
    httpClient.delete("/files/" + id);
  }
}
