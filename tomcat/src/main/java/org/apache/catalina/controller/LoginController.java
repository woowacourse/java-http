package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String USER = "user";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private final SessionManager sessionManager;

    public LoginController(final SessionManager sessionManager) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        StaticResources.serve(LOGIN_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Optional<User> user = authenticate(request);
        if (user.isEmpty()) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        response.sendRedirect(INDEX_PAGE);
        startNewSession(request, response, user.get());
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return sessionManager.findSession(request)
                .map(session -> session.getAttribute(USER))
                .isPresent();
    }

    private Optional<User> authenticate(final HttpRequest request) {
        final Optional<String> account = request.getBodyParameter(ACCOUNT);
        final Optional<String> password = request.getBodyParameter(PASSWORD);
        if (account.isEmpty() || password.isEmpty()) {
            log.info("login parameters are missing");
            return Optional.empty();
        }
        final Optional<User> user = InMemoryUserRepository.findByAccount(account.get())
                .filter(found -> found.checkPassword(password.get()));
        if (user.isEmpty()) {
            log.info("login failed");
        }
        return user;
    }

    // 로그인하면 기존 세션을 폐기하고 새 세션을 발급한다 (session fixation 방지)
    private void startNewSession(final HttpRequest request, final HttpResponse response, final User user) {
        sessionManager.findSession(request)
                .ifPresent(old -> sessionManager.remove(old.getId()));
        final Session session = sessionManager.create();
        session.setAttribute(USER, user);
        response.addCookie(sessionManager.toCookie(session));
        log.info("login success. account: {}", user.getAccount());
    }
}
