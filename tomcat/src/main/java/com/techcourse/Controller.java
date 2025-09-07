package com.techcourse;

import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    public HttpResponse hello() {
        return ResponseEntity.ok("Hello world!");
    }

    public HttpResponse signIn(Map<String, String> loginRequest) {
        User user = service.getUser(loginRequest);
        return ResponseEntity.ok(user.toString());
    }
}
