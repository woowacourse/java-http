package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> mappings = new HashMap<>();

    public RequestMapping() {
        UserService userService = new UserService();
        mappings.put("/login", new LoginController(userService));
        mappings.put("/register", new RegisterController(userService));
    }

    public Optional<Controller> getController(HttpRequest request) {
        String requestUri = request.getPath();
        Controller controller = mappings.getOrDefault(requestUri, new ResourceController());
        return Optional.ofNullable(controller);
    }
}
