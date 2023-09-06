package org.apache.coyote.handler;

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

public class LoginHandler extends RequestHandler {
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String COOKIE_KEY = "Cookie";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String SESSION_KEY = "JSESSIONID";

    public LoginHandler(String mappingUri) {
        this.mappingUri = mappingUri;
    }

    @Override
    public Http11Response doService(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getRequestLine().getHttpMethod();

        if (httpMethod.equals("GET")) {
            return doGet(httpRequest);
        }

        if (httpMethod.equals("POST")) {
            return doPost(httpRequest);
        }

        return redirectUnAuthorizedPage();
    }

    @Override
    public Http11Response doPost(final HttpRequest httpRequest) {
        final RequestBody body = httpRequest.getRequestBody();

        final String account = body.getByKey(ACCOUNT);
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        final String password = body.getByKey(PASSWORD);

        if (user.isPresent() && user.get().checkPassword(password)) {
            final Session session = Session.getInstance();
            session.setAttribute("user", user.get());
            sessionManager.add(session);

            return redirectIndexPageWithCookie(session);
        }

        return redirectUnAuthorizedPage();
    }

    @Override
    public Http11Response doGet(final HttpRequest httpRequest) {
        final RequestHeader header = httpRequest.getRequestHeader();

        if (header.getByKey(COOKIE_KEY) == null) {
            return loginPage();
        }

        final HttpCookie cookie = HttpCookie.from(header.getByKey(COOKIE_KEY));
        final String jSessionId = cookie.getByKey(SESSION_KEY);
        final Session session = findSessionById(jSessionId);

        if (session == null) {
            return loginPage();
        }

        return redirectIndexPageWithCookie(session);
    }

    private Http11Response redirectIndexPageWithCookie(final Session session) {
        final String resourcePath = RESOURCE_PATH + INDEX_PAGE;

        final Http11Response http11Response = new Http11Response(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
        http11Response.addCookie(SESSION_KEY, session.getId());
        return http11Response;
    }

    private Http11Response redirectUnAuthorizedPage() {
        final String resourcePath = RESOURCE_PATH + UNAUTHORIZED_PAGE;
        return new Http11Response(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }

    private Http11Response loginPage() {
        final String resourcePath = RESOURCE_PATH + LOGIN_PAGE;
        return new Http11Response(classLoader.getResource(resourcePath), 200, "OK");
    }

    private Session findSessionById(final String jSessionId) {
        if (sessionManager.findSession(jSessionId) == null) {
            throw new IllegalArgumentException("유효하지 않은 세션입니다.");
        }
        return sessionManager.findSession(jSessionId);
    }
}
