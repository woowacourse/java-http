package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ResponseCreator;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Controller {
    private final SessionManager sessionManager;
    private final ResponseCreator responseCreator;

    public Controller() {
        this.sessionManager = new SessionManager();
        this.responseCreator = new ResponseCreator();
    }

    public String getHelloWorldPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getUrl());
    }

    public String getDefaultPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getUrl());
    }

    public String getLoginPage(HttpRequest httpRequest) throws IOException {
        String sessionId = httpRequest.getSessionId();
        Session session = sessionManager.findSession(sessionId);
        if (session != null && session.getAttribute("user") != null) {
            return responseCreator.create(200, "/index.html"); // todo 리다이렉트
        }
        return responseCreator.create(200, httpRequest.getUrl());
    }

    public String login(HttpRequest httpRequest) throws IOException {
        Map<String, String> params = httpRequest.getBody();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));
        if (optionalUser.isEmpty()) {
            return responseCreator.create(401, "/401.html");
        }
        User user = optionalUser.get();
        if (user.checkPassword(params.get("password"))) {
            Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);
            System.out.println(user);
            return responseCreator.create(302, "/index.html", "JSESSIONID=" + session.getId());
        }
        return responseCreator.create(401, "/401.html"); //todo 리다이렉트
    }

    public String register(HttpRequest httpRequest) throws IOException {
        Map<String, String> payload = httpRequest.getBody();
        User user = new User(payload.get("account"), payload.get("password"), payload.get("email"));
        InMemoryUserRepository.save(user);

        return responseCreator.create(200, "/index.html");
    }
}
