package ai.rhesis.sdk.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MultipartBuilder {
  private final String boundary;
  private final byte[] separator;
  private final ByteArrayOutputStream buffer;

  public MultipartBuilder() {
    this.boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
    this.separator = ("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8);
    this.buffer = new ByteArrayOutputStream();
  }

  public MultipartBuilder addPart(String name, String filename, String contentType, byte[] value) {
    try {
      buffer.write(separator);
      StringBuilder header = new StringBuilder();
      header.append("Content-Disposition: form-data; name=\"").append(name).append("\"");
      if (filename != null) {
        header.append("; filename=\"").append(filename).append("\"");
      }
      header.append("\r\n");
      if (contentType != null) {
        header.append("Content-Type: ").append(contentType).append("\r\n");
      }
      header.append("\r\n");
      buffer.write(header.toString().getBytes(StandardCharsets.UTF_8));
      buffer.write(value);
      buffer.write("\r\n".getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException("Failed to add part to multipart body", e);
    }
    return this;
  }

  public MultipartBuilder addTextPart(String name, String value) {
    return addPart(name, null, null, value.getBytes(StandardCharsets.UTF_8));
  }

  public HttpRequest.BodyPublisher build() {
    try {
      buffer.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return HttpRequest.BodyPublishers.ofByteArray(buffer.toByteArray());
  }

  public String getBoundary() {
    return boundary;
  }
}
