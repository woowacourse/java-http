package com.techcourse.controller;

import com.techcourse.db.SessionManager;
import com.techcourse.service.Service;
import java.util.Map;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestController {

    private static final Logger log = LoggerFactory.getLogger(RestController.class);
    private final Service service;
    private final SessionManager sessionManager;

    public RestController(Service service, SessionManager sessionManager) {
        this.service = service;
        this.sessionManager = sessionManager;
    }

    public HttpResponse signIn(Map<String, String> signInRequest) {
        service.create(signInRequest);
        return ResponseEntity.found(Map.of("Location", "/index.html"));
    }
}
