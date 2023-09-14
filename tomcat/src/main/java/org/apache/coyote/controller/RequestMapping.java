package org.apache.coyote.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    final Map<String, Controller> requestMapping = new HashMap<>();

    public RequestMapping() {
        initiateMapping();
    }

    private void initiateMapping() {
        requestMapping.put("/login", new LoginController());
        requestMapping.put("/register", new RegisterController());
        requestMapping.put("/", new RootController());
        requestMapping.put("resource", new ResourceController());
    }

    public Controller getController(HttpRequest request) {
        Controller resourceController = requestMapping.get("resource");
        return requestMapping.getOrDefault(request.getPath(), resourceController);
    }

}
