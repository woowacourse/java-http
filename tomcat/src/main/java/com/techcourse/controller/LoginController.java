package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public final class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String SESSION_USER_ATTRIBUTE = "user";

    private final Controller staticResourceController;
    private final Manager sessionManager;

    public LoginController() {
        this(new StaticResourceController(), SessionManager.getInstance());
    }

    public LoginController(
            final Controller staticResourceController,
            final Manager sessionManager
    ) {
        this.staticResourceController = Objects.requireNonNull(staticResourceController);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        if (findSession(request).filter(this::isLoggedIn).isPresent()) {
            return HttpResponse.redirect(INDEX_PATH);
        }
        return staticResourceController.service(request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final var loginUser = findLoginUser(request);
        if (loginUser.isEmpty()) {
            return HttpResponse.redirect(UNAUTHORIZED_PATH);
        }

        final var existingSession = findSession(request);
        final var session = existingSession.orElseGet(sessionManager::createSession);
        final var user = loginUser.get();
        session.setAttribute(SESSION_USER_ATTRIBUTE, user);
        log.info("login user found: {}", user.getAccount());

        final var response = HttpResponse.redirect(INDEX_PATH);
        if (existingSession.isPresent()) {
            return response;
        }
        return response.addHeader("Set-Cookie", SESSION_COOKIE_NAME + "=" + session.getId());
    }

    private Optional<User> findLoginUser(final HttpRequest request) {
        final var account = request.parameter("account");
        final var password = request.parameter("password");
        if (account.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()));
    }

    private Optional<Session> findSession(final HttpRequest request) {
        return request.cookie(SESSION_COOKIE_NAME)
                .map(sessionManager::findSession);
    }

    private boolean isLoggedIn(final Session session) {
        return session.getAttribute(SESSION_USER_ATTRIBUTE) instanceof User;
    }
}
