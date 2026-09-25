package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.MethodDispatchingController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginController extends MethodDispatchingController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String JSESSION_ID = "JSESSIONID";
    private static final String SESSION_USER_ATTRIBUTE = "user";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String INDEX_PATH = "/index.html";

    private final SessionManager sessionManager;
    private final StaticResourceController staticResourceController;

    public LoginController(
            final SessionManager sessionManager,
            final StaticResourceController staticResourceController
    ) {
        this.sessionManager = sessionManager;
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (isLoggedIn(request)) {
            response.sendRedirect(INDEX_PATH);
            return;
        }
        staticResourceController.serve("/login.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Optional<User> authenticatedUser = findAuthenticatedUser(request.formParameters());
        if (authenticatedUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        final User user = authenticatedUser.get();
        log.info("로그인 성공! 아이디 : {}", user.getAccount());

        final Optional<Session> existingSession = findSession(request);
        final Session session = existingSession.orElseGet(this::createSession);
        session.setAttribute(SESSION_USER_ATTRIBUTE, user);

        response.sendRedirect(INDEX_PATH);
        if (existingSession.isEmpty()) {
            response.addHeader("Set-Cookie", JSESSION_ID + "=" + session.getId());
        }
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return findSession(request)
                .map(session -> session.getAttribute(SESSION_USER_ATTRIBUTE))
                .isPresent();
    }

    private Optional<Session> findSession(final HttpRequest request) {
        return request.cookie(JSESSION_ID)
                .map(sessionManager::findSession);
    }

    private Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }

    private Optional<User> findAuthenticatedUser(final Map<String, String> formParameters) {
        if (!hasCredentials(formParameters)) {
            return Optional.empty();
        }

        final String account = formParameters.get(ACCOUNT_PARAMETER);
        final String password = formParameters.get(PASSWORD_PARAMETER);
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private boolean hasCredentials(final Map<String, String> formParameters) {
        return formParameters.containsKey(ACCOUNT_PARAMETER)
                && formParameters.containsKey(PASSWORD_PARAMETER);
    }
}
