package org.apache.coyote.http11.responseline;

public class ResponseLine {

  private final String version;
  private final HttpStatus status;

  public ResponseLine(final String version, final HttpStatus status) {
    this.version = version;
    this.status = status;
  }

  public String build() {
    return this.version + " " + this.status.build();
  }
}
