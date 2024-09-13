package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.controller.AbstractController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.session.Session;

public class LoginController extends AbstractController {

    private static final String SESSION_USER_KEY = "user";
    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String LOGIN_SUCCESS_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    public static final String LOGIN_PATH = "/login";

    public LoginController() {
        super(LOGIN_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getQueryParamFromBody("account");
        String password = request.getQueryParamFromBody("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> {
                            if (user.checkPassword(password)) {
                                acceptLogin(request, response, user);
                                return;
                            }
                            response.sendRedirect(LOGIN_FAIL_PAGE);
                        },
                        () -> response.sendRedirect(LOGIN_FAIL_PAGE)
                );
    }

    private void acceptLogin(HttpRequest request, HttpResponse response, User user) {
        Session session = request.getSession();
        session.setAttribute(SESSION_USER_KEY, user);
        response.addCookie(Session.SESSION_COOKIE_KEY, session.getId());
        response.sendRedirect(LOGIN_SUCCESS_PAGE);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (hasUser(request)) {
            response.sendRedirect(LOGIN_SUCCESS_PAGE);
            return;
        }
        response.addStaticBody(LOGIN_PAGE);
    }

    private boolean hasUser(HttpRequest request) {
        Session session = request.getSession();
        return session.hasAttribute(SESSION_USER_KEY);
    }
}
