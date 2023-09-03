package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.service.LoginService;

public class HandlerMapper {

    private final Map<String, Controller> controllerByPath = new HashMap<>();

    public HandlerMapper() {
        controllerByPath.put("/login", new LoginController(new LoginService()));
    }

    public boolean haveAvailableHandler(Request requestLine) {
        return controllerByPath.keySet().contains(requestLine.getRequestLine().getPath());
    }

    public Controller getHandler(Request request) {
        return controllerByPath.get(request.getRequestLine().getPath());
    }
}
