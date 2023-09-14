package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.Http11Response;

import java.util.Optional;

public class LoginController extends AbstractController {
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String COOKIE_KEY = "Cookie";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String SESSION_KEY = "JSESSIONID";

    @Override
    public void doPost(final HttpRequest httpRequest, final Http11Response httpResponse) {
        final RequestBody body = httpRequest.getRequestBody();

        final String account = body.getByKey(ACCOUNT);
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        final String password = body.getByKey(PASSWORD);

        if (user.isPresent() && user.get().checkPassword(password)) {
            final Session session = Session.getInstance();
            session.setAttribute("user", user.get());
            sessionManager.add(session);

            redirectIndexPageWithCookie(session, httpResponse);
            return;
        }

        redirectUnAuthorizedPage(httpResponse);
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final Http11Response httpResponse) {
        final RequestHeader header = httpRequest.getRequestHeader();

        if (header.getByKey(COOKIE_KEY) == null) {
            loginPage(httpResponse);
            return;
        }

        final HttpCookie cookie = HttpCookie.from(header.getByKey(COOKIE_KEY));
        final String jSessionId = cookie.getByKey(SESSION_KEY);
        final Session session = findSessionById(jSessionId);

        if (session == null) {
            loginPage(httpResponse);
            return;
        }

        redirectIndexPageWithCookie(session, httpResponse);
    }

    private void redirectIndexPageWithCookie(final Session session, final Http11Response httpResponse) {
        final String resourcePath = RESOURCE_PATH + INDEX_PAGE;
        httpResponse.addCookie(SESSION_KEY, session.getId());
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(302);
        httpResponse.setStatusMessage(HTTP_FOUND);
    }

    private void redirectUnAuthorizedPage(final Http11Response httpResponse) {
        final String resourcePath = RESOURCE_PATH + UNAUTHORIZED_PAGE;
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(302);
        httpResponse.setStatusMessage(HTTP_FOUND);
    }

    private void loginPage(final Http11Response httpResponse) {
        final String resourcePath = RESOURCE_PATH + LOGIN_PAGE;
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(200);
        httpResponse.setStatusMessage("OK");
    }

    private Session findSessionById(final String jSessionId) {
        if (sessionManager.findSession(jSessionId) == null) {
            throw new IllegalArgumentException("유효하지 않은 세션입니다.");
        }
        return sessionManager.findSession(jSessionId);
    }
}
