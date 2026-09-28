package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RegisterController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler =
            new StaticResourceHandler();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        staticResourceHandler.serve("static/register.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 Parameter가 누락되었습니다.");
        }

        final User user = new User(account, password, email);
        if (!InMemoryUserRepository.saveIfAbsent(user)) {
            response.setStatus(409, "Conflict");
            response.setHeader("Content-Type", "text/plain;charset=utf-8");
            response.setBody("이미 사용 중인 아이디입니다.".getBytes(StandardCharsets.UTF_8));
            return;
        }

        response.sendRedirect("/index.html");
    }
}
