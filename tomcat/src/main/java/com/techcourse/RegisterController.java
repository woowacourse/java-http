package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.mvc.controller.AbstractController;

public class RegisterController extends AbstractController {

    private static final int FOUND = 302;
    private static final String LOGIN_SUCCESS_PATH = "/index.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBody("account");
        String password = request.getBody("password");
        String email = request.getBody("email");

        InMemoryUserRepository.save(
                new User(null, account, password, email)
        );

        response.setStatus(FOUND);
        response.setLocation(LOGIN_SUCCESS_PATH);
    }
}
