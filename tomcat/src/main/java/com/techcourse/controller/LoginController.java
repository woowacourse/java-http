package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String USER_SESSION_KEY = "user";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private final SessionManager sessionManager;

    public LoginController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {

        final Optional<String> sessionId = request.getCookie(JSESSIONID);

        if (sessionId.isEmpty()) {
            return;
        }

        final HttpSession session = sessionManager.findSession(sessionId.get());

        if (session == null) {
            return;
        }

        if (getUser(session) == null) {
            return;
        }

        response.sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {

        final String account = request.getParameter(ACCOUNT).orElse(null);

        final String password = request.getParameter(PASSWORD).orElse(null);

        if (account == null || password == null) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("login failed account: {}", account);
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final User loginUser = user.get();

        final HttpSession session = sessionManager.createSession();

        session.setAttribute(USER_SESSION_KEY, loginUser);

        response.addHeader(SET_COOKIE, JSESSIONID + "=" + session.getId());

        log.info("login success account: {}", loginUser.getAccount());

        response.sendRedirect(INDEX_PAGE);
    }

    private User getUser(final HttpSession session) {
        final Object value = session.getAttribute(USER_SESSION_KEY);

        if (value instanceof User user) {
            return user;
        }

        return null;
    }
}