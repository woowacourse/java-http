package com.techcourse;

import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Service service;

    public RestController(Service service) {
        this.service = service;
    }

    public HttpResponse hello() {
        return ResponseEntity.ok("Hello world!");
    }

    public HttpResponse login(Map<String, String> loginRequest) {
        try {
            User user = service.getUser(loginRequest);
            log.info("{}", user.toString());
            return ResponseEntity.found(Map.of("Location", "/index.html"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.found(Map.of("Location", "/401.html"));
        }
    }

    public HttpResponse signIn(Map<String, String> signInRequest) {
        service.create(signInRequest);
        return ResponseEntity.found(Map.of("Location", "/index.html"));
    }
}
