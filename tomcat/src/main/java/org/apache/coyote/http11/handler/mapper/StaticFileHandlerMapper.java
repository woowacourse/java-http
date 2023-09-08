package org.apache.coyote.http11.handler.mapper;

import org.apache.coyote.http11.controller.StaticFileController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticFileHandlerMapper implements HandlerMapper {

  private static final StaticFileController STATIC_FILE_CONTROLLER = new StaticFileController();

  @Override
  public boolean isSupport(final HttpRequest request) {
    return request.isGetMethod() && request.isEndWith(".html", ".css", ".js");
  }

  @Override
  public HttpResponse handle(final HttpRequest request) throws Exception {
    return STATIC_FILE_CONTROLLER.service(request);
  }
}
