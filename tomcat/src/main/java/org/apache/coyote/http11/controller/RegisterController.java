package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.HttpUtils.parseParam;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

  private static final String INDEX_PAGE = "/index.html";
  private static final String REGISTER_PAGE = "/register.html";

  @Override
  protected HttpResponse doPost(final HttpRequest request) {
    final Map<String, String> params = parseParam(request.getBody());
    final String account = params.get("account");
    final String password = params.get("password");
    final String email = params.get("email");
    final User user = new User(account, password, email);
    InMemoryUserRepository.save(user);

    final HttpHeader header = new HttpHeader();
    header.setHeaderLocation(INDEX_PAGE);
    return responseFoundRedirect(header);
  }

  @Override
  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    return responseStaticFile(request, REGISTER_PAGE);
  }
}
