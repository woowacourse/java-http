package org.apache.coyote.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> SUIT_CASE = new HashMap<>();

    static {
        SUIT_CASE.put("/login", new LoginController());
        SUIT_CASE.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        if (SUIT_CASE.containsKey(request.getPath())) {
            return SUIT_CASE.get(request.getPath());
        }
        return new DefaultController();
    }
}
