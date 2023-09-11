package org.apache.coyote.http11.responseline;

public enum HttpStatus {
  OK(200, "OK"),
  FOUND(302, "FOUND"),
  UNAUTHORIZED(401, "UNAUTHORIZED"),
  INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),
  NOT_FOUND(404, "NOT FOUND");

  private final int statusCode;
  private final String message;

  HttpStatus(final int statusCode, final String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public String build() {
    return statusCode + " " + message;
  }
}
