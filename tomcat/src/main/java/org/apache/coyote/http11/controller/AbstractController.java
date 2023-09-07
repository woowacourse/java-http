package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

  @Override
  public HttpResponse service(final HttpRequest request) {
    if (request.getMethod().equalsIgnoreCase("GET")) {
      return doGet(request);
    }
    if (request.getMethod().equalsIgnoreCase("POST")) {
      return doPost(request);
    }
    return null;
  }

  protected HttpResponse doPost(final HttpRequest request) {
    throw new UnsupportedOperationException();
  }

  protected HttpResponse doGet(final HttpRequest request) {
    throw new UnsupportedOperationException();
  }
}
