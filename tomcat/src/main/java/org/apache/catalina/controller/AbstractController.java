package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

  @Override
  public void service(final HttpRequest request, final HttpResponse response) {
    if (request.isGetMethod()) {
      doGet(request, response);
    }
    if (request.isPostMethod()) {
      doPost(request, response);
    }
  }

  protected void doPost(final HttpRequest request, final HttpResponse response) {
    throw new UnsupportedOperationException();
  }

  protected void doGet(final HttpRequest request, final HttpResponse response) {
    throw new UnsupportedOperationException();
  }
}
