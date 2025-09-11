package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.net.URL;
import java.nio.file.Path;

public class RegisterController extends AbstractController {

    @Override
    public boolean support(HttpRequest request) {
        return request.getRequestUrl()
                .startsWith("/register");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        URL resource = getClass().getClassLoader()
                .getResource("static/register.html");
        Path resourcePath = Path.of(resource.getPath());

        response.ok()
                .writeStaticResource(resourcePath);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        InMemoryUserRepository.save(new User(account, password, email));

        response.redirect("http://localhost:8080/index.html");
    }
}
