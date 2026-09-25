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

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (isAuthenticated(request)) {
            response.sendRedirect("/index.html");
            return;
        }

        InputStream resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static/login.html");

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
        String account = parameters.get("account");
        String password = parameters.get("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> login(user, response),
                        () -> response.sendRedirect("/401.html")
                );
    }

    private boolean isAuthenticated(HttpRequest request) {
        if (!request.hasJsessionId()) {
            return false;
        }

        Session session = SessionManager.findSession(request.getJsessionId());
        return session != null && session.getAttribute("loginUser") != null;
    }

    private void login(User user, HttpResponse response) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("loginUser", user);
        SessionManager.add(session);

        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
        response.sendRedirect("/index.html");
    }
}
