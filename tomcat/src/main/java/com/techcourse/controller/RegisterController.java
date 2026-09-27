package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.controller.AbstractController;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(readRegisterPage());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        handleRegister(request, response);
    }

    private void handleRegister(HttpRequest request, HttpResponse response) {
        Map<String, String> params = parseParams(request.getBody());
        User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);

        response.redirectTo("/index.html");
    }

    private Map<String, String> parseParams(String body) {
        return Arrays.stream(body.split("&"))
                .map(parameterPair -> parameterPair.split("="))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private String readRegisterPage() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        if (resource == null) {
            throw new IllegalStateException("register page not found");
        }
        try (var inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
