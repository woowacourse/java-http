package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.detector.FileDetector;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements DynamicHandler {

  private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.getHttpMethod().equals("GET")
        && httpRequest.getPath().startsWith("/login");
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final String account = httpRequest.getQueryString().get("account");

    final User user = InMemoryUserRepository.findByAccount(account)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    log.info("user = {}", user);

    final String responseBody = FileDetector.detect("static/login.html");

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: text/html;charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
