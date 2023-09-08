package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.HttpUtils.parseParam;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;
import org.apache.coyote.http11.responseline.ResponseLine;

public class LoginController extends AbstractController {

  private static final String INDEX_PAGE = "/index.html";
  private static final String LOGIN_PAGE = "/login.html";
  private static final String UNAUTHORIZED_PAGE = "/401.html";
  private static final String JSESSIONID = "JSESSIONID";

  @Override
  protected HttpResponse doPost(final HttpRequest request) {
    final String body = request.getBody();
    final Map<String, String> params = parseParam(body);
    final String account = params.get("account");
    final String password = params.get("password");

    if (account == null || password == null) {
      final ResponseLine responseLine = new ResponseLine(HttpStatus.FOUND);
      final HttpHeader header = new HttpHeader();
      header.setHeaderLocation(INDEX_PAGE);
      return new HttpResponse(responseLine, header);
    }

    final Optional<User> user = InMemoryUserRepository.findByAccount(account);
    if (user.isPresent() && user.get().checkPassword(password)) {
      final String uuid = UUID.randomUUID().toString();
      final Session session = new Session(uuid);
      request.addSession(session);

      final ResponseLine responseLine = new ResponseLine(HttpStatus.FOUND);
      final HttpHeader header = new HttpHeader();
      header.setHeaderLocation(INDEX_PAGE);
      header.setCookie(JSESSIONID + "=" + uuid);
      return new HttpResponse(responseLine, header);
    }

    final ResponseLine responseLine = new ResponseLine(HttpStatus.FOUND);
    final HttpHeader header = new HttpHeader();
    header.setHeaderLocation(UNAUTHORIZED_PAGE);
    return new HttpResponse(responseLine, header);
  }

  @Override
  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    final String sessionId = request.getCookie(JSESSIONID);
    if (request.getSession(sessionId) == null) {
      return responseStaticFile(request, LOGIN_PAGE);
    }
    final ResponseLine responseLine = new ResponseLine(HttpStatus.FOUND);
    final HttpHeader header = new HttpHeader();
    header.setHeaderLocation(INDEX_PAGE);
    return new HttpResponse(responseLine, header);
  }
}
