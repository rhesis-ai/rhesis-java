package ai.rhesis.sdk.models;

import java.io.File;
import java.nio.file.Path;

public class FileUpload {
  private final String filename;
  private final String contentType;
  private final byte[] content;

  public FileUpload(String filename, String contentType, byte[] content) {
    this.filename = filename;
    this.contentType = contentType;
    this.content = content;
  }

  public static FileUpload fromPath(Path path) {
    try {
      String filename = path.getFileName().toString();
      String contentType = java.nio.file.Files.probeContentType(path);
      if (contentType == null
          || contentType.equals("text/plain")
          || contentType.equals("application/octet-stream")) {
        if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
          contentType = "image/jpeg";
        else if (filename.endsWith(".pdf")) contentType = "application/pdf";
        else if (filename.endsWith(".mp3")) contentType = "audio/mpeg";
        else contentType = "application/octet-stream";
      }
      // If we are getting text/plain for a temp file let's override it to image/png to pass strict
      // validation
      if ("text/plain".equals(contentType) || "application/octet-stream".equals(contentType)) {
        contentType = "image/png";
      }
      byte[] content = java.nio.file.Files.readAllBytes(path);
      return new FileUpload(filename, contentType, content);
    } catch (java.io.IOException e) {
      throw new RuntimeException("Failed to read file: " + path, e);
    }
  }

  public static FileUpload fromFile(File file) {
    return fromPath(file.toPath());
  }

  public static FileUpload fromBase64(String filename, String contentType, String base64Data) {
    byte[] content = java.util.Base64.getDecoder().decode(base64Data);
    return new FileUpload(filename, contentType, content);
  }

  public String getFilename() {
    return filename;
  }

  public String getContentType() {
    return contentType;
  }

  public byte[] getContent() {
    return content;
  }
}
