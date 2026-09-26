package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.IOException;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        StaticResourceController.serve("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            response.sendError(HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 입력해 주세요.");
            return;
        }

        InMemoryUserRepository.save(new User(account, password, request.getParameter("email")));
        response.sendRedirect("/index.html");
    }
}
