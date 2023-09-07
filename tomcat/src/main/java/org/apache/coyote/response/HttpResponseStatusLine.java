package org.apache.coyote.response;

public class HttpResponseStatusLine {

  private final Protocol protocol;
  private final HttpStatus httpStatus;

  private HttpResponseStatusLine(final HttpStatus httpStatus) {
    this.protocol = Protocol.HTTP_1_1;
    this.httpStatus = httpStatus;
  }

  public static HttpResponseStatusLine ok() {
    return new HttpResponseStatusLine(HttpStatus.OK);
  }

  public static HttpResponseStatusLine redirect() {
    return new HttpResponseStatusLine(HttpStatus.FOUND);
  }

  public String read() {
    return protocol.read() + httpStatus.read() + " ";
  }
}
