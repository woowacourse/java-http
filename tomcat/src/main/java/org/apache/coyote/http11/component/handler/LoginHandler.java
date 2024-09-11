package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.common.Method;
import org.apache.coyote.http11.component.exception.AuthenticationException;
import org.apache.coyote.http11.component.exception.NotFoundException;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;
import org.apache.coyote.http11.component.response.ResponseHeader;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.session.CustomSession;
import com.techcourse.session.SessionManager;

public class LoginHandler implements HttpHandler {

    private static final String LOGIN_SUCCESSFUL_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_SUCCESSFUL_COOKIE_NAME = "JSESSIONID";
    private static final String LOGIN_HTML = "login.html";
    private static final String ID_QUERY_NAME = "account";
    private static final String PASSWORD_QUERY_NAME = "password";

    private final String path;

    public LoginHandler(final String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isSameMethod(Method.POST)) {
            return doPost(request);
        }
        return doGet(request);
    }

    private HttpResponse doPost(final HttpRequest request) {
        final var identifier = request.getBodyContent(ID_QUERY_NAME);
        final var password = request.getBodyContent(PASSWORD_QUERY_NAME);
        validateUser(identifier, password);
        return renderSuccessful(identifier);
    }

    private HttpResponse doGet(final HttpRequest request) {
        if (isLogin(request.getCookieContent(LOGIN_SUCCESSFUL_COOKIE_NAME))) {
            return StaticResourceFinder.renderRedirect(LOGIN_SUCCESSFUL_REDIRECT_URI);
        }
        return StaticResourceFinder.render(LOGIN_HTML);
    }

    private boolean isLogin(final String sessionId) {
        if (sessionId.isBlank()) {
            return false;
        }
        final SessionManager sessionManager = SessionManager.getInstance();
        try {
            sessionManager.findSession(sessionId);
            return true;
        } catch (final IllegalArgumentException exception) {
            return false;
        }
    }

    private HttpResponse renderSuccessful(final String identifier) {
        final ResponseHeader responseHeader = new ResponseHeader();
        final var SessionId = createSession(identifier);
        responseHeader.put("Set-Cookie", LOGIN_SUCCESSFUL_COOKIE_NAME + "=" + SessionId);
        responseHeader.put("Location", LOGIN_SUCCESSFUL_REDIRECT_URI);
        return StaticResourceFinder.renderRedirect(responseHeader);
    }

    private String createSession(final String identifier) {
        final var user = InMemoryUserRepository.findByAccount(identifier)
                .orElseThrow(NotFoundException::new);
        final var session = new CustomSession();
        session.setAttribute("user", user);
        final var sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        return sessionManager.getSessionId(session);
    }

    private void validateUser(final String identifier, final String password) {
        final var user = InMemoryUserRepository.findByAccount(identifier)
                .orElseThrow(AuthenticationException::new);
        if (!user.checkPassword(password)) {
            throw new AuthenticationException();
        }
    }
}
