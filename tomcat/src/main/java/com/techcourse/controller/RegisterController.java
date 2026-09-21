package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected String doGet(HttpRequest request, HttpResponse response) {
        return "/register.html";
    }

    @Override
    protected String doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyValue("account");
        String email = request.getBodyValue("email");
        String password = request.getBodyValue("password");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return "redirect:/index.html";
    }

}
