package nextstep.jwp.controller;

import static org.apache.coyote.http11.ParseUtils.parseParam;
import static org.apache.coyote.http11.header.HeaderType.COOKIE;
import static org.apache.coyote.http11.header.HeaderType.LOCATION;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.header.HeaderType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.responseline.HttpStatus;

public class LoginController extends AbstractController {

  private static final String INDEX_PAGE = "/index.html";
  private static final String LOGIN_PAGE = "/login.html";
  private static final String UNAUTHORIZED_PAGE = "/401.html";
  private static final String JSESSIONID = "JSESSIONID";

  private static boolean isInvalidAccountOrPassword(final String account, final String password) {
    if (account == null || account.isBlank() || password == null || password.isBlank()) {
      return true;
    }
    final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
    return optionalUser.isEmpty() || !optionalUser.get().checkPassword(password);
  }

  private static boolean isNotExistSession(final HttpRequest request) {
    final String cookie = request.getHeader(COOKIE);
    if (cookie == null) {
      return true;
    }

    final String sessionId = cookie.split(";")[0].split("=")[1];
    return request.findSession(sessionId).isEmpty();
  }

  @Override
  protected void doPost(final HttpRequest request, final HttpResponse response) {
    final String body = request.getBody();
    final Map<String, String> params = parseParam(body);
    final String account = params.get("account");
    final String password = params.get("password");

    if (isInvalidAccountOrPassword(account, password)) {
      response.setStatus(HttpStatus.FOUND);
      response.setHeader(LOCATION, UNAUTHORIZED_PAGE);
      return;
    }

    final Session session = Session.generateSession();
    request.addSession(session);
    response.setStatus(HttpStatus.FOUND);
    response.setHeader(LOCATION, INDEX_PAGE);
    response.setHeader(HeaderType.SET_COOKIE, JSESSIONID + "=" + session.getId());
  }

  @Override
  protected void doGet(final HttpRequest request, final HttpResponse response) {
    if (isNotExistSession(request)) {
      response.setBodyAsStaticFile(LOGIN_PAGE);
      return;
    }

    response.setStatus(HttpStatus.FOUND);
    response.setHeader(LOCATION, INDEX_PAGE);
  }
}
