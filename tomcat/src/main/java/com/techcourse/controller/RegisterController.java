package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.controller.AbstractController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String REGISTER_SUCCESS_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String REGISTER_PATH = "/register";

    public RegisterController() {
        super(REGISTER_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getQueryParamFromBody("account");
        String email = request.getQueryParamFromBody("email");
        String password = request.getQueryParamFromBody("password");

        validateDuplicateAccount(account);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.sendRedirect(REGISTER_SUCCESS_PAGE);
    }

    private void validateDuplicateAccount(String account) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(foundUser -> {
                    throw new IllegalArgumentException("중복된 account 입니다.");
                });
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.addStaticBody(REGISTER_PAGE);
    }
}
