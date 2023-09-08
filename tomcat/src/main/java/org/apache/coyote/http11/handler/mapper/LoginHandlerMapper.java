package org.apache.coyote.http11.handler.mapper;

import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginHandlerMapper implements HandlerMapper {

  private static final LoginController LOGIN_CONTROLLER = new LoginController();
  private static final String LOGIN_URL = "/login";

  private static boolean isSupportMethod(final HttpRequest request) {
    return request.isGetMethod() || request.isPostMethod();
  }

  @Override
  public boolean isSupport(final HttpRequest request) {
    return isSupportMethod(request) && request.isSameUrl(LOGIN_URL);
  }

  @Override
  public HttpResponse handle(final HttpRequest request) throws Exception {
    return LOGIN_CONTROLLER.service(request);
  }
}
