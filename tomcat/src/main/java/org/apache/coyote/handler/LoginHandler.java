package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements DynamicHandler {

  private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isPostMethod() && httpRequest.isStartWith("/login");
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final RequestBody requestBody = httpRequest.getRequestBody();
    final String account = requestBody.getValue("account");
    final String password = requestBody.getValue("password");

    final User user = InMemoryUserRepository.findByAccount(account)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    if (user.checkPassword(password)) {
      final Session session = httpRequest.getSession();
      session.setAttribute("user", user);
      httpRequest.addCookie(session.getId());

      return String.join("\r\n",
          "HTTP/1.1 302 FOUND ",
          "Set-Cookie: " + httpRequest.getCookie(),
          "Location: http://localhost:8080/index.html ");
    }

    return String.join("\r\n",
        "HTTP/1.1 302 FOUND ",
        "Location: http://localhost:8080/401.html ");
  }
}
