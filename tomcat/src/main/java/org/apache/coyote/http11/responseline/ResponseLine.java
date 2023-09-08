package org.apache.coyote.http11.responseline;

public class ResponseLine {

  private static final String HTTP_1_1 = "HTTP/1.1";

  private final String version;
  private final HttpStatus status;

  public ResponseLine(final String version, final HttpStatus status) {
    this.version = version;
    this.status = status;
  }

  public ResponseLine(final HttpStatus status) {
    this(HTTP_1_1, status);
  }

  public String build() {
    return this.version + " " + this.status.build();
  }
}
