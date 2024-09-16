package com.techcourse.controller;

import static com.techcourse.controller.RequestPath.INDEX;
import static com.techcourse.controller.RequestPath.LOGIN;
import static com.techcourse.controller.RequestPath.UNAUTHORIZED;

import java.io.IOException;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String SESSION_KEY = "user";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private final Manager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (isLoggedIn(request)) {
            response.sendRedirect(INDEX.path());
            return;
        }
        response.sendRedirect(LOGIN.path());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        if (login(request, response)) {
            return;
        }
        response.sendRedirect(UNAUTHORIZED.path());
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final Session requestSession = request.getSession(false);
        if (requestSession == null) {
            return false;
        }
        final Session savedSession = sessionManager.findSession(requestSession.getId());
        if (savedSession == null) {
            return false;
        }
        User user = (User) savedSession.getAttribute(SESSION_KEY);
        log.info("user: {}", user);
        return user != null;
    }

    private boolean login(final HttpRequest request, final HttpResponse response) {
        HttpQueryParams queryParams = request.getQueryParamsFromBody();
        if (queryParams == null) {
            return false;
        }
        User user = getUser(queryParams);
        if (user == null) {
            return false;
        }
        String password = queryParams.get(PASSWORD);
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            saveUserInSession(response, user);
            return true;
        }
        return false;
    }

    private User getUser(final HttpQueryParams queryParams) {
        String account = queryParams.get(ACCOUNT);
        return InMemoryUserRepository.findByAccount(account).orElse(null);
    }

    private void saveUserInSession(final HttpResponse response, final User user) {
        Session session = new Session();
        session.setAttribute(SESSION_KEY, user);
        sessionManager.add(session);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));
        response.sendRedirect(INDEX.path());
    }
}
