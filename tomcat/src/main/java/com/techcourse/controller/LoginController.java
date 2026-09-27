package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";

    private static final String USER_ATTRIBUTE = "user";

    private static final String COOKIE_HEADER = "Cookie";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "static/login.html";


    private final Manager sessionManager;

    public LoginController(final Manager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var cookie = new HttpCookie(request.getHeader(COOKIE_HEADER));
        if (isLoggedIn(cookie)) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        final var resource = getClass().getClassLoader().getResource(LOGIN_PAGE);
        response.setBody(Files.readAllBytes(Path.of(resource.toURI())));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final var loginUser = login(request.getParameters());
        if (loginUser.isEmpty()) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        final var session = sessionManager.createSession();
        session.setAttribute(USER_ATTRIBUTE, loginUser.get());
        response.setHeader(SET_COOKIE_HEADER, HttpCookie.JSESSIONID + "=" + session.getId());
        response.sendRedirect(INDEX_PAGE);
        log.info("Session id: {} -> {}", session.getId(), session.getAttribute(USER_ATTRIBUTE));
    }

    private Optional<User> login(final Map<String, String> formParameters) {
        final var account = formParameters.get(ACCOUNT_PARAMETER);
        final var password = formParameters.get(PASSWORD_PARAMETER);
        if (account == null || password == null) {
            return Optional.empty();
        }
        final var user = InMemoryUserRepository.findByAccount(account)
                .filter(found -> found.checkPassword(password));
        user.ifPresent(found -> log.info("login success: {}", found));
        return user;
    }

    private boolean isLoggedIn(HttpCookie cookie) throws IOException {
        if (!cookie.hasJSessionId()) {
            return false;
        }
        final var jSessionId = cookie.getJSessionId();
        final var session = sessionManager.findSession(jSessionId);
        if (session == null) {
            return false;
        }
        return session.getAttribute(USER_ATTRIBUTE) != null;
    }
}
