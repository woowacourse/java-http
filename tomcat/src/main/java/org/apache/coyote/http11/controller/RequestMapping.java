package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.http.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController());
    }

    public Controller getController(final HttpRequest httpRequest) {
        String uri = httpRequest.getUri();
        return controllers.getOrDefault(uri, new ResourceController());
    }
}
