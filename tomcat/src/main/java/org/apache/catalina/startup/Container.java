package org.apache.catalina.startup;

import java.io.IOException;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Container {

    private final ControllerMapper controllerMapper;

    private Container(final ControllerMapper controllerMapper) {
        this.controllerMapper = controllerMapper;
    }

    public static Container from(final Map<String, Controller> controllers) {
        return new Container(new ControllerMapper(controllers));
    }

    public void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final Controller controller = controllerMapper.findController(request);
        if (controller == null) {
            return;
        }
        controller.service(request, response);
    }
}
