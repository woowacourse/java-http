package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.responseline.ResponseLine;

public class HttpResponse {

  private final ResponseLine responseLine;
  private final HttpHeader headers;
  private final String body;

  public HttpResponse(
      final ResponseLine responseLine,
      final HttpHeader headers,
      final String body
  ) {
    this.responseLine = responseLine;
    this.headers = headers;
    this.body = body;
  }

  public HttpResponse(final ResponseLine responseLine, final HttpHeader headers) {
    this(responseLine, headers, "");
  }

  public String build() {
    return String.join(System.lineSeparator(),
        this.responseLine.build(),
        this.headers.build(),
        "",
        this.body);
  }
}
