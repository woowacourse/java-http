package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticFileController extends AbstractController {


  @Override
  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    return responseStaticFile(request, request.getUrl());
  }
}
