package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.servlet.http.request.HttpRequest;
import org.apache.catalina.servlet.http.response.HttpResponse;
import org.apache.catalina.servlet.http.response.HttpStatus;

public class RegisterController {

    public String getRegisterPage() {
        return "/register";
    }

    public void register(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        InMemoryUserRepository.save(new User(account, password, email));
        response.setStatus(HttpStatus.OK.getStatusCode());
        response.sendRedirect("/index.html");
    }
}
