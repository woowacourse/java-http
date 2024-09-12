package org.apache.coyote.http11.dispatcher;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final ConcurrentHashMap<String, Controller> controllerMapper = new ConcurrentHashMap<>();

    public RequestMapping() {
        controllerMapper.put("/login", new LoginController());
        controllerMapper.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        return Optional.ofNullable(controllerMapper.get(request.getRequestUrl()))
                .orElse(new AbstractController());
    }
}
