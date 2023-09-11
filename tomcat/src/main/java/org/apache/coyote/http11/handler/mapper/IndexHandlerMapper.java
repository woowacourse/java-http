package org.apache.coyote.http11.handler.mapper;

import nextstep.jwp.controller.IndexController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexHandlerMapper implements HandlerMapper {

  private static final Controller controller = new IndexController();

  @Override
  public boolean isSupport(final HttpRequest request) {
    return request.isGetMethod() &&
        (request.isUrlEndWith("/") || request.isUrlEndWith("/index.html"));
  }

  @Override
  public void handle(final HttpRequest request, final HttpResponse response) {
    controller.service(request, response);
  }
}
