package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        StaticResourceController.serve("/login.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            response.sendRedirect("/401.html");
            return;
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password))
                .orElse(null);
        if (user == null) {
            response.sendRedirect("/401.html");
            return;
        }

        request.renewSession().setAttribute("user", user);
        log.info("회원 조회 성공: {}", user.getAccount());
        response.sendRedirect("/index.html");
    }
}
