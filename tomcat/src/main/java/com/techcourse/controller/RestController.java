package com.techcourse.controller;

import com.techcourse.db.Session;
import com.techcourse.db.SessionManager;
import com.techcourse.model.User;
import com.techcourse.service.Service;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Service service;
    private final SessionManager sessionManager;

    public RestController(Service service, SessionManager sessionManager) {
        this.service = service;
        this.sessionManager = sessionManager;
    }

    public HttpResponse hello() {
        return ResponseEntity.ok("Hello world!");
    }

    public HttpResponse login(Map<String, String> loginRequest) {
        try {
            User user = service.getUser(loginRequest);
            log.info("{}", user.toString());
            UUID sessionId = UUID.randomUUID();
            Session session = new Session(sessionId.toString());
            sessionManager.add(session);

            Map<String, String> headers = Map.of(
                    "Location", "/index.html",
                    "Set-Cookie", "JSESSIONID=" + sessionId
            );
            return ResponseEntity.found(headers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.found(Map.of("Location", "/401.html"));
        }
    }

    public HttpResponse signIn(Map<String, String> signInRequest) {
        service.create(signInRequest);
        return ResponseEntity.found(Map.of("Location", "/index.html"));
    }
}
