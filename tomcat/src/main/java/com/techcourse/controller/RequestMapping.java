package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<URI, Controller> mappings = new HashMap<>();

    public RequestMapping() {
        UserService userService = new UserService();
        mappings.put(URI.create("/login"), new LoginController(userService));
        mappings.put(URI.create("/register"), new RegisterController(userService));
    }

    public Optional<Controller> getController(HttpRequest request) {
        URI requestUri = request.getUri();
        return Optional.ofNullable(mappings.get(requestUri));
    }
}
