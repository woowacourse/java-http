package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.HttpUtils.readContentsFromFile;

import java.io.IOException;
import org.apache.coyote.http11.HttpUtils;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;

public class StaticFileController extends AbstractController {


  @Override
  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    final String body = readContentsFromFile(request.getUrl());
    final ResponseLine responseLine = new ResponseLine(HttpStatus.OK);
    final HttpHeader header = new HttpHeader();
    final String contentType = HttpUtils.getContentType(request.getHeader("Accept"));
    header.setHeader("Content-Type", contentType + ";charset=utf-8");
    header.setHeader("Content-Length", body.getBytes().length + "");
    return new HttpResponse(responseLine, header, body);
  }
}
