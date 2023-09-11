package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.header.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HeaderType.CONTENT_TYPE;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HeaderType;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;
import org.apache.coyote.http11.utils.IOUtils;

public class HttpResponse {

  private final ResponseLine responseLine;
  private final HttpHeader header;
  private String body;

  public HttpResponse() {
    this.responseLine = new ResponseLine();
    this.header = new HttpHeader();
    this.body = "";
  }

  public void setBodyAsStaticFile(final String fileName) {
    final String contents = IOUtils.readContentsFromFile(fileName);
    this.body = contents;
    setStatus(HttpStatus.OK);
    final ContentType contentType = ContentType.from(fileName);
    setHeader(CONTENT_TYPE, contentType.getValue() + "; charset=utf-8");
    setHeader(CONTENT_LENGTH, String.valueOf(contents.length()));
  }

  public void setBodyAsString(final String contents) {
    this.body = contents;
    setStatus(HttpStatus.OK);
    setHeader(CONTENT_LENGTH, String.valueOf(contents.length()));
  }

  public void setStatus(final HttpStatus httpStatus) {
    responseLine.setStatus(httpStatus);
  }

  public void setHeader(final HeaderType key, final String value) {
    header.setHeader(key, value);
  }
  
  public String build() {
    return String.join(System.lineSeparator(),
        responseLine.build(),
        this.header.build(),
        "",
        this.body);
  }
}
