package ai.rhesis.sdk.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConnectionType {
  REST("REST"),
  WEBSOCKET("WebSocket"),
  GRPC("GRPC"),
  SDK("SDK");

  private final String value;

  ConnectionType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
