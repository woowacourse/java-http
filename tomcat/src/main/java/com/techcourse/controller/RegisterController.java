package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.servlet.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setStaticResourceResponse("/register.html");
        response.write();
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        try {
            register(account, password, email);
            response.sendRedirect("/index.html");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("/400.html");
        }
        response.write();
    }

    private void register(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account).ifPresent(user -> {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        });

        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
    }
}
