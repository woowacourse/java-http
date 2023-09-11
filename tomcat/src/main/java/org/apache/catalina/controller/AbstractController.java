package org.apache.catalina.controller;

import static org.apache.coyote.http11.ParseUtils.parseContentType;
import static org.apache.coyote.http11.header.HeaderType.ACCEPT;
import static org.apache.coyote.http11.utils.IOUtils.readContentsFromFile;

import java.io.IOException;
import org.apache.coyote.http11.header.HeaderType;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.requestline.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;

public abstract class AbstractController implements Controller {

  protected static HttpResponse responseStaticFile(final HttpRequest request, final String path)
      throws IOException {
    final String body = readContentsFromFile(path);
    final String contentType = parseContentType(request.getHeader(ACCEPT));
    final ResponseLine responseLine = new ResponseLine(HttpStatus.OK);
    final HttpHeader header = new HttpHeader();
    header.setHeader(HeaderType.CONTENT_TYPE, contentType + ";charset=utf-8");
    header.setHeader(HeaderType.CONTENT_LENGTH, body.getBytes().length + "");
    return new HttpResponse(responseLine, header, body);
  }

  protected static HttpResponse responseFoundRedirect(final HttpHeader header) {
    final ResponseLine responseLine = new ResponseLine(HttpStatus.FOUND);
    return new HttpResponse(responseLine, header);
  }

  @Override
  public HttpResponse service(final HttpRequest request) throws IOException {
    if (request.getMethod().equals(HttpMethod.GET)) {
      return doGet(request);
    }
    if (request.getMethod().equals(HttpMethod.POST)) {
      return doPost(request);
    }
    return null;
  }

  protected HttpResponse doPost(final HttpRequest request) {
    throw new UnsupportedOperationException();
  }

  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    throw new UnsupportedOperationException();
  }
}
