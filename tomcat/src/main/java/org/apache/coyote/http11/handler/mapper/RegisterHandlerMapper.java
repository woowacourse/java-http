package org.apache.coyote.http11.handler.mapper;

import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterHandlerMapper implements HandlerMapper {

  private static final RegisterController REGISTER_CONTROLLER = new RegisterController();
  private static final String REGISTER_URL = "/register";

  private static boolean isSupportMethod(final HttpRequest request) {
    return request.isGetMethod() || request.isPostMethod();
  }

  @Override
  public boolean isSupport(final HttpRequest request) {
    return isSupportMethod(request) && request.isSameUrl(REGISTER_URL);
  }

  @Override
  public HttpResponse handle(final HttpRequest request) throws Exception {
    return REGISTER_CONTROLLER.service(request);
  }
}
