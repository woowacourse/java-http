package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Session> session = request.getSession();
        if (session.isPresent()) {
            response.sendRedirect("/index.html");
            return;
        }
        response.sendStaticResource("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<String> account = request.getQueryParameter("account");
        Optional<String> password = request.getQueryParameter("password");
        if (account.isEmpty() || password.isEmpty() || notExistUser(account.get(), password.get())) {
            setUnauthorized(response);
            return;
        }
        doLogin(account.get(), password.get(), response);
    }

    private void setUnauthorized(HttpResponse response) {
        response.sendStaticResource(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private boolean notExistUser(String account, String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password)
                .isEmpty();
    }

    private void doLogin(String account, String password, HttpResponse response) {
        User user = InMemoryUserRepository.findByAccountAndPassword(account, password)
                .orElseThrow();
        Session session = createUserSession(user);

        response.sendRedirect("/index.html");
        response.setSession(session);
    }

    private Session createUserSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        return session;
    }
}
