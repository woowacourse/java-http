package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<HttpEndpoint, Controller> ENDPOINT_CONTROLLER_MAPPING = new HashMap<>();
    private static final RequestMapping INSTANCE = new RequestMapping();

    private RequestMapping() {
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public void registerEndPoint(HttpEndpoint endpoint, Controller controller) {
        ENDPOINT_CONTROLLER_MAPPING.put(endpoint, controller);
    }

    public Controller getController(HttpRequest httpRequest) {
        return ENDPOINT_CONTROLLER_MAPPING.get(HttpEndpoint.of(httpRequest));
    }
}
