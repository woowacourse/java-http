package org.apache.catalina.servlet.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.resource.StaticResourceFinder;
import org.apache.tomcat.http.exception.AuthenticationException;
import org.apache.tomcat.http.exception.NotFoundException;
import org.apache.tomcat.http.response.ResponseHeader;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.session.CustomSession;
import com.techcourse.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String LOGIN_SUCCESSFUL_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_SUCCESSFUL_COOKIE_NAME = "JSESSIONID";
    private static final String LOGIN_HTML = "login.html";
    private static final String ID_QUERY_NAME = "account";
    private static final String PASSWORD_QUERY_NAME = "password";

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        if (isLogin(request)) {
            final var render = StaticResourceFinder.renderRedirect(LOGIN_SUCCESSFUL_REDIRECT_URI);
            response.copyProperties(render);
            return;
        }
        final var render = StaticResourceFinder.render(LOGIN_HTML);
        response.copyProperties(render);
    }

    private boolean isLogin(final HttpRequest request) {
        try {
            final var sessionId = request.getCookieContent(LOGIN_SUCCESSFUL_COOKIE_NAME);
            final SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.findSession(sessionId);
            return true;
        } catch (final IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final var identifier = request.getBodyContent(ID_QUERY_NAME);
        final var password = request.getBodyContent(PASSWORD_QUERY_NAME);
        validateUser(identifier, password);
        final HttpResponse httpResponse = renderSuccessful(identifier);
        response.copyProperties(httpResponse);
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
