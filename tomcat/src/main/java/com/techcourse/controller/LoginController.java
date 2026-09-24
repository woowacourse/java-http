package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_PAGE = "/login";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_ATTRIBUTE = "user";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private final ResourceRenderer renderer = new ResourceRenderer();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (isLoggedIn(request.getCookie())) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        renderer.render(LOGIN_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Optional<User> user = login(request);
        if (user.isEmpty()) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        final Session session = createSession(user.get());
        response.sendRedirect(INDEX_PAGE);
        response.setCookie(JSESSIONID + "=" + session.getId());
    }

    private boolean isLoggedIn(final HttpCookie cookie) {
        if (!cookie.hasJSessionId()) {
            return false;
        }
        final Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
        return session != null && session.getAttribute(USER_ATTRIBUTE) != null;
    }

    private Optional<User> login(final HttpRequest request) {
        final Map<String, String> params = request.getBodyParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get(ACCOUNT))
                .filter(it -> it.checkPassword(params.get(PASSWORD)));
        user.ifPresent(it -> log.info("{}", it));
        return user;
    }

    private Session createSession(final User user) {
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER_ATTRIBUTE, user);
        SessionManager.getInstance().add(session);
        return session;
    }
}