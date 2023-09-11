package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Controller.DefaultController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> mapper = new HashMap<>();

    public void put(final String path, final Controller controller) {
        this.mapper.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        final String resource = request.getRequestPath().getResource();
        return mapper.getOrDefault(resource, new DefaultController());
    }
}
