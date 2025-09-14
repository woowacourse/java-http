package com.techcourse.controller;

import com.techcourse.ResponseWriters;
import com.techcourse.db.Session;
import com.techcourse.db.SessionManager;
import com.techcourse.model.User;
import com.techcourse.service.Service;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final Service service;
    private final SessionManager sessionManager;

    public LoginController(Service service, SessionManager sessionManager) {
        this.service = service;
        this.sessionManager = sessionManager;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> headers = request.getHeaders();
        if (existsSession(headers)) {
            ResponseWriters.ok(response, "index.html");
        }
        ResponseWriters.ok(response, "login.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> loginRequest = request.parseForBody();
        try {
            User user = service.getUser(loginRequest);
            log.info("{}", user.toString());
            UUID sessionId = UUID.randomUUID();
            Session session = new Session(sessionId.toString());
            session.setAttribute("user", user);
            sessionManager.addIfAbsent(session);

            ResponseWriters.found(response, "/index.html");
        } catch (IllegalArgumentException e) {
            ResponseWriters.found(response, "/401.html");
        }
    }

    private boolean existsSession(Map<String, String> headers) {
        String cookie = headers.get("Cookie");
        if (cookie == null) {
            return false;
        }

        String sessionId = HttpCookie.extractCookieValue(cookie, "JSESSIONID");
        if (sessionId == null) {
            return false;
        }

        Session session = sessionManager.findSession(sessionId);
        return session != null;
    }
}
