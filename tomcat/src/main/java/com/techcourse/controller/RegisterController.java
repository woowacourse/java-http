package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.controller.AbstractController;
import jakarta.http.ContentType;
import jakarta.http.HttpBody;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(readResource("register.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        registerNewUser(request);
        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(readResource("index.html"));
    }

    private void registerNewUser(HttpRequest request) {
        HttpBody body = request.getHttpBody();
        String account = body.get("account")
                .orElseThrow(() -> new IllegalArgumentException("account 값은 필수입니다."));
        String password = body.get("password")
                .orElseThrow(() -> new IllegalArgumentException("password 값은 필수입니다."));
        String email = body.get("email")
                .orElseThrow(() -> new IllegalArgumentException("email 값은 필수입니다."));

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
