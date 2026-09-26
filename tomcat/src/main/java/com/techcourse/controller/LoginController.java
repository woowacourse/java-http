package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String SESSION_USER_KEY = "user";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parameters = request.getParameters();
        Optional<User> user = login(parameters);
        if (user.isPresent()) {
            Session session = request.getSession(true);
            session.setAttribute(SESSION_USER_KEY, user.get());

            response.sendRedirect(INDEX_PAGE);
            response.addCookie(session.getId());
        } else {
            response.sendRedirect(UNAUTHORIZED_PAGE);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.sendRedirect(INDEX_PAGE);
        } else {
            response.forward(request.getPath());
        }
    }

    private Optional<User> login(Map<String, String> parameters) {
        String account = parameters.get(ACCOUNT);
        String password = parameters.get(PASSWORD);
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private boolean isLoggedIn(HttpRequest request) {
        Session session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }
}
