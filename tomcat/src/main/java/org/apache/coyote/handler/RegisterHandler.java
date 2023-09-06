package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;

public class RegisterHandler implements DynamicHandler {

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isPostMethod() && httpRequest.isSameUri("/register");
  }

  @Override
  public String handle(final HttpRequest httpRequest) throws IOException {
    final RequestBody requestBody = httpRequest.getRequestBody();
    final String account = requestBody.getValue("account");
    final String password = requestBody.getValue("password");
    final String email = requestBody.getValue("email");

    InMemoryUserRepository.save(new User(account, password, email));

    return String.join("\r\n",
        "HTTP/1.1 302 FOUND ",
        "Location: http://localhost:8080/index.html ");
  }
}
