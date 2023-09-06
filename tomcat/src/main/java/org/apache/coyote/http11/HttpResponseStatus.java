package org.apache.coyote.http11;

public enum HttpResponseStatus {
  OK(200, "OK"),
  FOUND(302, "Found"),
  UNAUTHORIZATION(401, "Unauthorization"),
  NOT_FOUND(404, "Not Found"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error");
  private final int code;
  private final String message;

  HttpResponseStatus(final int code, final String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public String toString() {
    return code + " " + message;
  }
}
