package ai.rhesis.sdk.exceptions;

public class RhesisApiException extends RuntimeException {
  private final int statusCode;
  private final String responseBody;

  public RhesisApiException(String message, int statusCode, String responseBody) {
    super(message + " (Status " + statusCode + "): " + responseBody);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResponseBody() {
    return responseBody;
  }
}
