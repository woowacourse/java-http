package org.apache.coyote.http11.handler.mapper;

import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticFileHandlerMapper implements HandlerMapper {

  private static final Controller controller = new StaticFileController();

  @Override
  public boolean isSupport(final HttpRequest request) {
    return request.isGetMethod() && (request.isUrlEndWith(".js") || request.isUrlEndWith(".html")
        || request.isUrlEndWith(".css"));
  }

  @Override
  public void handle(final HttpRequest request, final HttpResponse response) {
    controller.service(request, response);
  }
}
