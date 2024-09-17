package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
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
