package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.sendRedirect("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getBodyParam("account");
        String password = request.getBodyParam("password");
        String email = request.getBodyParam("email");
        if (account != null && password != null && email != null) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            log.info("회원가입 완료: {}", user);
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/register.html");
    }
}
