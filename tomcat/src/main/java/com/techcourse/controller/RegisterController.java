package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        InputStream resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static/register.html");

        if (resourceStream == null) {
            response.notFound();
            return;
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(resourceStream)) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            response.ok("text/html;charset=utf-8", body);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> parameters = request.getBodyParameters();
        User user = InMemoryUserRepository.save(new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        ));

        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("loginUser", user);
        SessionManager.add(session);

        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
        response.sendRedirect("/index.html");
    }
}
