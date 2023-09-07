package org.apache.catalina.servlet;

import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestAdapter implements Adapter {

    private static final Map<String, Controller> container = new HashMap<>();

    @Override
    public Controller getController(HttpRequest request) {
        String requestUri = request.getRequestUri();
        return container.getOrDefault(requestUri, new StaticResourceController());
    }

    @Override
    public void addController(String path, Controller controller) {
        container.put(path, controller);
    }
}
