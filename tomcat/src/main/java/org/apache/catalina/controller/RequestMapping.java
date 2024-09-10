package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<HttpEndpoint, Controller> endpointControllers;

    public RequestMapping(Map<HttpEndpoint, Controller> endpointControllers) {
        this.endpointControllers = endpointControllers;
    }

    public static RequestMapping of(List<AbstractController> controllers) {
        Map<HttpEndpoint, Controller> endpointControllers = new HashMap<>();
        for (AbstractController controller : controllers) {
            controller.getEndpoints()
                    .forEach(endpoint -> endpointControllers.put(endpoint, controller));
        }
        return new RequestMapping(endpointControllers);
    }

    public Controller getController(HttpRequest request) {
        HttpEndpoint httpEndpoint = HttpEndpoint.of(request);
        Controller controller = endpointControllers.get(httpEndpoint);
        if (controller == null) {
            return new ResourceController();
        }
        return controller;
    }
}
