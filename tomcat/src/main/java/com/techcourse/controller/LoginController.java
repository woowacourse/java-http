package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    public static final String HTML_SUFFIX = ".html";
    public static final String USER_SESSION_NAME = "user";
    public static final String ACCOUNT_PARAM_NAME = "account";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String ACCESS_DENIED_PAGE = "/401.html";
    public static final String INDEX_PAGE = "/index.html";

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> params = request.parseRequestQuery();

        InMemoryUserRepository.findByAccount(params.get(ACCOUNT_PARAM_NAME)).ifPresentOrElse(user -> {
            if (user.checkPassword(params.get(PASSWORD_PARAM_NAME))) {
                Session session = createSession(user);
                buildLoginSuccessResponse(response, session);
                return;
            }
            buildLoginFailResponse(response);
        }, () -> buildLoginFailResponse(response));
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLogin(request, response)) {
            return;
        }
        final var path = request.getRequestPath();
        response.ok();
        response.setContentType(FileType.HTML);
        response.setContentOfResources(path + HTML_SUFFIX);
    }

    private Session createSession(final User user) {
        final UUID id = UUID.randomUUID();
        final Session session = new Session(id.toString());
        session.setAttribute(USER_SESSION_NAME, user);
        SessionManager.add(session);
        log.info("user: {}", user);

        return session;
    }

    private boolean isLogin(final HttpRequest request, final HttpResponse response) {
        final var cookies = request.getCookies();
        if (cookies.containsSession() && hasSession(cookies)) {
            final var session = SessionManager.findSession(cookies.getSessionCookie());
            buildLoginSuccessResponse(response, session);
            return true;
        }
        return false;
    }

    private boolean hasSession(final HttpCookie cookies) {
        final var sessionId = cookies.getSessionCookie();
        return SessionManager.contains(sessionId);
    }

    private void buildLoginSuccessResponse(final HttpResponse response, final Session session) {
        response.found();
        addCookieOnResponseHeader(response, session);
        response.sendRedirect(INDEX_PAGE);
    }

    private void addCookieOnResponseHeader(final HttpResponse response, final Session session) {
        HttpCookie cookie = HttpCookie.from(session);
        log.info("cookie: {}", session.getId());

        response.addCookies(cookie);
    }

    private void buildLoginFailResponse(final HttpResponse response) {
        response.found();
        response.sendRedirect(ACCESS_DENIED_PAGE);
    }
}
