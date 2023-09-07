package org.apache.coyote.response;

public enum HttpStatus {

  OK("200","OK"),
  FOUND("302","Found")
  ;

  private final String statusCode;
  private final String statusMessage;

  HttpStatus(final String statusCode, final String statusMessage) {
    this.statusCode = statusCode;
    this.statusMessage = statusMessage;
  }

  public String read() {
    return statusCode + " " + statusMessage;
  }
}
