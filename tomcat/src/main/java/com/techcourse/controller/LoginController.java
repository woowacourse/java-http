package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request.getHeaders())) {
            response.redirectTo("/index.html");
            return;
        }
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(readLoginPage());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpHeaders requestHeaders = request.getHeaders();
        HttpCookie cookie = HttpCookie.from(requestHeaders.get("Cookie"));
        String sessionId = cookie.get("JSESSIONID");

        Session session = sessionManager.findSession(sessionId);
        if (session != null && getUser(session) != null) {
            response.redirectTo("/index.html");
            return;
        }

        Map<String, String> params = parseParams(request.getBody());
        Optional<User> user = login(params.get("account"), params.get("password"));
        if (user.isEmpty()) {
            response.redirectTo("/401.html");
            return;
        }

        response.redirectTo("/index.html");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        }

        Session loginSession = new Session(sessionId);
        loginSession.setAttribute("user", user.get());
        sessionManager.add(loginSession);
    }

    private boolean isLoggedIn(HttpHeaders requestHeaders) {
        HttpCookie cookie = HttpCookie.from(requestHeaders.get("Cookie"));
        Session session = sessionManager.findSession(cookie.get("JSESSIONID"));
        return session != null && getUser(session) != null;
    }

    private Optional<User> login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));
        user.ifPresent(foundUser -> log.info("user={}", foundUser));
        return user;
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private Map<String, String> parseParams(String body) {
        return Arrays.stream(body.split("&"))
                .map(parameterPair -> parameterPair.split("="))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private String readLoginPage() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        if (resource == null) {
            throw new IllegalStateException("login page not found");
        }
        try (var inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
