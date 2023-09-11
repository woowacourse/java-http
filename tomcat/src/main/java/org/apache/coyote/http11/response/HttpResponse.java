package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.header.HeaderType.CONTENT_LENGTH;

import org.apache.coyote.http11.header.HeaderType;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;
import org.apache.coyote.http11.utils.IOUtils;

public class HttpResponse {

  private final ResponseLine responseLine;
  private final HttpHeader headers;
  private String body;

  public HttpResponse(
      final ResponseLine responseLine,
      final HttpHeader headers,
      final String body
  ) {
    this.responseLine = responseLine;
    this.headers = headers;
    this.body = body;
  }

  public void setBodyAsStaticFile(final String fileName) {
    final String contents = IOUtils.readContentsFromFile(fileName);
    this.body = contents;
    setStatus(HttpStatus.OK);
    setHeader(CONTENT_LENGTH, String.valueOf(contents.length()));
  }

  public void setStatus(final HttpStatus httpStatus) {
    responseLine.setStatus(httpStatus);
  }

  public void setHeader(final HeaderType key, final String value) {
    headers.setHeader(key, value);
  }

  public void putCookie(final String key, final String value) {
    headers.putCookie(key, value);
  }
}
