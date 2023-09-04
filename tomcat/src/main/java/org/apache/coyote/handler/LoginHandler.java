package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements DynamicHandler {

  private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    final String httpMethod = httpRequest.getHttpMethod();
    final String path = httpRequest.getPath();
    return httpMethod.equals("GET") && path.startsWith("/login");
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final String account = httpRequest.getQueryString().get("account");

    final User user = InMemoryUserRepository.findByAccount(account)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    log.info("user = {}", user);

    final URL resource = getClass()
        .getClassLoader()
        .getResource("static/login.html");

    final String responseBody = new String(
        Files.readAllBytes(new File(resource.getFile()).toPath()));

    return String.join("\r\n",
        "HTTP/1.1 200 OK ",
        "Content-Type: text/html;charset=utf-8 ",
        "Content-Length: " + responseBody.getBytes().length + " ",
        "",
        responseBody);
  }
}
