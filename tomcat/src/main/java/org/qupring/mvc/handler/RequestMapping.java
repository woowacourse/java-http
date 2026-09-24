package org.qupring.mvc.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.request.HttpRequest;
import org.qupring.mvc.controller.Controller;

public class RequestMapping {

    private final Map<String, Controller> controllerMappings = new HashMap<>();
    private final Map<String, String> resourceMappings = new HashMap<>();

    public void addController(String path, Controller controller) {
        controllerMappings.put(path, controller);
    }

    public Controller getController(HttpRequest request) {
        return controllerMappings.get(request.getUrl());
    }

    public void addResourceMappings(Map<String, String> mappings) {
        resourceMappings.putAll(mappings);
    }

    public String getResource(String path) {
        return resourceMappings.get(path);
    }
}
