package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            handleRegister(request, response);
        } else {
            response.sendRedirect("/register.html");
        }
    }

    private void handleRegister(HttpRequest request, HttpResponse response) {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");
        String email = request.getQueryParam("email");

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
