package com.techcourse.controller;

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

    private static final String RESOURCE_PATH = "/login.html";
    private static final String ROOT_RESOURCE_PATH = "/index.html";

    private final Manager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (isLoggedIn(request)) {
            response.sendRedirect(ROOT_RESOURCE_PATH);
            return;
        }
        response.sendRedirect(RESOURCE_PATH);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        if (login(request, response)) {
            return;
        }
        response.sendRedirect("/401.html");
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
        User user = (User) savedSession.getAttribute("user");
        log.info("user: {}", user);
        return user != null;
    }

    private boolean login(final HttpRequest request, final HttpResponse response) throws IOException {
        HttpQueryParams queryParams = request.getQueryParamsFromBody();
        if (queryParams == null) {
            return false;
        }
        User user = getUser(queryParams);
        if (user == null) {
            return false;
        }
        String password = queryParams.get("password");
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            saveUserInSession(response, user);
            return true;
        }
        return false;
    }

    private User getUser(final HttpQueryParams queryParams) {
        String account = queryParams.get("account");
        return InMemoryUserRepository.findByAccount(account).orElse(null);
    }

    private void saveUserInSession(final HttpResponse response, final User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));
        response.sendRedirect(ROOT_RESOURCE_PATH);
    }
}
