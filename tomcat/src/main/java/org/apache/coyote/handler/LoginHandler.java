package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.Session;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.HttpResponseStatusLine;

public class LoginHandler implements DynamicHandler {

  private static final String SUCCESS_REDIRECT_URL = "http://localhost:8080/index.html";
  private static final String FAIL_REDIRECT_URL = "http://localhost:8080/401.html";

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isPostMethod() && httpRequest.isStartWith("/login");
  }

  @Override
  public void handle(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) throws IOException {
    final RequestBody requestBody = httpRequest.getRequestBody();
    final String account = requestBody.getValue("account");
    final String password = requestBody.getValue("password");

    InMemoryUserRepository.findByAccount(account)
        .ifPresentOrElse(
            user -> {
              if (user.checkPassword(password)) {
                redirectOnSuccessAuthenticate(httpRequest, httpResponse, user);
                return;
              }
              redirectOnFailure(httpResponse);
            },
            () -> redirectOnFailure(httpResponse)
        );
  }

  private void redirectOnSuccessAuthenticate(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse,
      final User user
  ) {
    final Session session = httpRequest.getSession();
    session.setAttribute("user", user);

    final Cookie cookie = httpRequest.getCookie();
    cookie.putJSessionId(session.getId());

    final HttpResponseHeader responseHeader = new HttpResponseHeader()
        .addCookie(cookie)
        .sendRedirect(SUCCESS_REDIRECT_URL);
    final HttpResponseStatusLine responseStatusLine = HttpResponseStatusLine.redirect();

    httpResponse.setHttpResponseHeader(responseHeader);
    httpResponse.setHttpResponseStatusLine(responseStatusLine);
  }

  private void redirectOnFailure(final HttpResponse httpResponse) {
    final HttpResponseHeader responseHeader = new HttpResponseHeader()
        .sendRedirect(FAIL_REDIRECT_URL);
    final HttpResponseStatusLine responseStatusLine = HttpResponseStatusLine.redirect();

    httpResponse.setHttpResponseStatusLine(responseStatusLine);
    httpResponse.setHttpResponseHeader(responseHeader);
  }
}
