package org.apache.coyote.http11.handler.mapper;

import nextstep.jwp.controller.LoginController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginHandlerMapper implements HandlerMapper {

  private static final String URL = "/login";
  private static final Controller controller = new LoginController();

  @Override
  public boolean isSupport(final HttpRequest request) {
    return request.isUrlEndWith(URL) && (request.isGetMethod() || request.isPostMethod());
  }

  @Override
  public void handle(final HttpRequest request, final HttpResponse response) {
    controller.service(request, response);
  }
}
