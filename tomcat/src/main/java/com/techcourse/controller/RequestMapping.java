package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.httprequest.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        throw new NotFoundException("존재하지 않는 경로입니다.");
    }
}
