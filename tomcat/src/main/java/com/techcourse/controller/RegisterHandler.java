package com.techcourse.controller;

import org.apache.coyote.handler.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterHandler extends AbstractController {
    private static final RegisterHandler INSTANCE = new RegisterHandler();

    private RegisterHandler() {
    }

    public static RegisterHandler getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getFormBodyByKey("account");
        String email = request.getFormBodyByKey("email");
        String password = request.getFormBodyByKey("password");
        InMemoryUserRepository.save(new User(account, password, email));

        response.setResponse(HttpResponse.builder().foundBuild("/index.html"));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder().foundBuild("/register.html"));
    }
}
