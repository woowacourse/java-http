package com.techcourse.controller;

import com.techcourse.ResponseWriters;
import com.techcourse.db.Session;
import com.techcourse.db.SessionManager;
import com.techcourse.model.User;
import com.techcourse.service.Service;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
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
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> headers = request.getHeaders();
        if (existsSession(headers)) {
            ResponseWriters.ok(response, "index.html");
        }
        ResponseWriters.ok(response, "login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> loginRequest = request.getBodyMap();
        try {
            User user = service.getUser(loginRequest);
            log.info("{}", user.toString());
            UUID sessionId = UUID.randomUUID();
            Session session = new Session(sessionId.toString());
            session.setAttribute("user", user);
            sessionManager.add(session);

            ResponseWriters.found(response, "/index.html"); // TODO 2025. 9. 12. 04:39: 그냥 fileNmae으로 path 찾도록 내부로직 수정하기
        } catch (IllegalArgumentException e) {
            ResponseWriters.found(response, "/401.html");
        }
    }

    private boolean existsSession(Map<String, String> headers) { // TODO 2025. 9. 12. 04:39: parsing의 책임은 cookie의 것이 아닌가
        String cookie = headers.get("Cookie");

        if (cookie == null) {
            return false;
        }

        String[] parsedCookie = cookie.split("; "); // Idea-1f980704=d1410481-d266-4764-a4dd-47a3d9d19f64; Pycharm-edf2faa0=91c6849a-33b8-4d86-a27b-bd16d51f090a; Webstorm-b369078d=8a15b985-71c2-42b0-96b4-eb3e64f0dfe5; JSESSIONID=d4d9915e-323d-43af-beb0-a60ac9e7c6b7
        for (String parsedValue : parsedCookie) {
            String[] splits = parsedValue.split("=", 2);
            String name = splits[0];
            String value = splits[1];

            if (name.equals("JSESSIONID")) {
                Session session = sessionManager.findSession(value);
                if (session == null) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
