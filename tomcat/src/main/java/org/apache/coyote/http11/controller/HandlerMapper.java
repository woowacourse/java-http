package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.service.LoginService;

public class HandlerMapper {

    private final Map<String, Controller> controllerByPath = new HashMap<>();

    public HandlerMapper() {
        controllerByPath.put("/login", new LoginController(new LoginService()));
    }

    public boolean haveAvailableHandler(RequestLine requestLine) {
        return controllerByPath.keySet().contains(requestLine.getPath());
    }

    public Controller getHandler(RequestLine requestLine) {
        return controllerByPath.get(requestLine.getPath());
    }
}
