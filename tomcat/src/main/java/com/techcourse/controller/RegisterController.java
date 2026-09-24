package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        renderStaticResource(response, "/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        final var email = request.getParameter("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 정보가 올바르지 않습니다.");
        }

        InMemoryUserRepository.save(new User(account, password, email));
        redirect(response, "/index.html");
    }
}
