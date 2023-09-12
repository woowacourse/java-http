package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;

public class RegisterHandler implements DynamicHandler {

  private static final String REDIRECT_URL = "/index.html";

  @Override
  public boolean canHandle(final HttpRequest httpRequest) {
    return httpRequest.isPostMethod() && httpRequest.isSameUri("/register");
  }

  @Override
  public void handle(
      final HttpRequest httpRequest,
      final HttpResponse httpResponse
  ) throws IOException {
    final RequestBody requestBody = httpRequest.getRequestBody();

    final String account = requestBody.getValue("account");
    final String password = requestBody.getValue("password");
    final String email = requestBody.getValue("email");

    InMemoryUserRepository.save(new User(account, password, email));

    httpResponse.redirect(REDIRECT_URL);
  }
}
