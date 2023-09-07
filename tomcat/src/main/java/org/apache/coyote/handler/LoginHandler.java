package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.Session;
import org.apache.coyote.response.ResponseCookie;
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
  public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
    final RequestBody requestBody = httpRequest.getRequestBody();
    final String account = requestBody.getValue("account");
    final String password = requestBody.getValue("password");

    final User user = InMemoryUserRepository.findByAccount(account)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    //TODO : 유저가 없으면 401로 리다이렉트
    if (user.checkPassword(password)) {
      final Session session = httpRequest.getSession();
      session.setAttribute("user", user);

      final HttpResponseHeader responseHeader = new HttpResponseHeader()
          .addCookie(new ResponseCookie(httpRequest.getCookie().getValue()))
          .sendRedirect(SUCCESS_REDIRECT_URL);
      final HttpResponseStatusLine responseStatusLine = HttpResponseStatusLine.redirect();

      httpResponse.setHttpResponseHeader(responseHeader);
      httpResponse.setHttpResponseStatusLine(responseStatusLine);

      return;
    }

    final HttpResponseHeader responseHeader = new HttpResponseHeader()
        .sendRedirect(FAIL_REDIRECT_URL);
    final HttpResponseStatusLine responseStatusLine = HttpResponseStatusLine.redirect();

    httpResponse.setHttpResponseStatusLine(responseStatusLine);
    httpResponse.setHttpResponseHeader(responseHeader);
  }
}
