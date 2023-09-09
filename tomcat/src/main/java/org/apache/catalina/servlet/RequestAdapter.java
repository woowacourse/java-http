package org.apache.catalina.servlet;

import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestAdapter implements Adapter {

    private static final Map<String, Controller> container = new HashMap<>();

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String requestUri = request.getRequestUri();
        Controller controller = container.getOrDefault(requestUri, new StaticResourceController());
        controller.service(request, response);
    }

    @Override
    public void addController(String path, Controller controller) {
        container.put(path, controller);
    }
}
