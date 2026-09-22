package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        Path filePath = Path.of(resource.toURI());
        String content = Files.readString(filePath, StandardCharsets.UTF_8);

        response.ok("text/html;charset=utf-8", content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> parameters = request.getBody().parseFormData();

        String account = parameters.getOrDefault("account", "");
        String email = parameters.getOrDefault("email", "");
        String password = parameters.getOrDefault("password", "");

        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);

        response.redirect("/index.html");
    }
}
