package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request.getSession())) {
            response.sendRedirect("/index.html");
            return;
        }
        response.setStaticResource(request.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<User> account = findAccount(request.getParameter("account"), request.getParameter("password"));
        if (account.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        request.getSession().setAttribute("user", account.get());
        response.sendRedirect("/index.html");
    }

    private boolean isLoggedIn(Session session) {
        return session.getAttribute("user") != null;
    }

    private Optional<User> findAccount(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            return user;
        }

        return Optional.empty();
    }
}
