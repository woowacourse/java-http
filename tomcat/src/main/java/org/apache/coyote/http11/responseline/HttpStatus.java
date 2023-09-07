package org.apache.coyote.http11.responseline;

public enum HttpStatus {
  OK(200, "OK"),
  FOUND(302, "FOUNT"),
  UNAUTHORIZED(401, "UNAUTHORIZED");

  private final int statusCode;
  private final String message;

  HttpStatus(final int statusCode, final String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public String getMessage() {
    return this.message;
  }

  public String build() {
    return this.statusCode + " " + this.message;
  }
}
