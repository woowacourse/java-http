package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.getStaticResource("/register.html");
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> bodys = request.parseQueryParameters();

        String account = bodys.get("account");
        String password = bodys.get("password");
        String email = bodys.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.redirect("/index.html");
    }
}
