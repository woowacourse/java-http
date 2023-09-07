package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.HttpResponseStatusLine;

public class RegisterHandler implements DynamicHandler {

  private static final String REDIRECT_URL = "http://localhost:8080/index.html";

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

    final HttpResponseHeader header = new HttpResponseHeader().sendRedirect(
        REDIRECT_URL);

    final HttpResponseStatusLine statusLine = HttpResponseStatusLine.redirect();

    httpResponse.setHttpResponseHeader(header);
    httpResponse.setHttpResponseStatusLine(statusLine);
  }
}
