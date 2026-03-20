package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.http.InternalHttpClient;
import ai.rhesis.sdk.models.FileUpload;
import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    // Determine if we have files to upload
    boolean hasFiles = test.files() != null && !test.files().isEmpty();

    // Create the test without files first
    Test testToCreate =
        hasFiles
            ? new Test(
                test.id(),
                test.testConfiguration(),
                test.behavior(),
                test.category(),
                test.topic(),
                test.testType(),
                test.prompt(),
                test.metadata(),
                null)
            : test;

    Test created = httpClient.post("/tests/", testToCreate, Test.class);

    // Upload files if any
    if (hasFiles) {
      List<FileUpload> fileUploads =
          test.files().stream()
              .map(
                  pathStr -> {
                    Path path = Paths.get(pathStr);
                    FileUpload upload = FileUpload.fromPath(path);
                    String contentType = upload.getContentType();
                    if ("text/plain".equals(contentType)
                        || "application/octet-stream".equals(contentType)) {
                      contentType = "image/png";
                    }
                    return new FileUpload(upload.getFilename(), contentType, upload.getContent());
                  })
              .toList();
      addFiles(created.id(), fileUploads);
    }

    return created;
  }

  public void delete(String id) {
    httpClient.delete("/tests/" + id);
  }

  public List<File> addFiles(String testId, List<FileUpload> files) {
    return new FileClient(httpClient).upload(files, testId, "Test");
  }

  public List<File> addFile(String testId, java.nio.file.Path path) {
    return addFiles(testId, List.of(FileUpload.fromPath(path)));
  }

  public List<File> addFile(String testId, java.io.File file) {
    return addFiles(testId, List.of(FileUpload.fromFile(file)));
  }

  public List<File> addFileFromBase64(
      String testId, String filename, String contentType, String base64Data) {
    return addFiles(testId, List.of(FileUpload.fromBase64(filename, contentType, base64Data)));
  }

  public List<File> getFiles(String testId) {
    return httpClient.get("/tests/" + testId + "/files", new TypeReference<List<File>>() {});
  }

  public void deleteFile(String fileId) {
    new FileClient(httpClient).delete(fileId);
  }
}
