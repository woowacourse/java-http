package org.apache.catalina.startup;

import java.io.IOException;
import java.util.List;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Container {

    private final ControllerMapper controllerMapper;

    public Container(final List<Controller> controllers) {
        controllerMapper = new ControllerMapper(controllers);
    }

    public void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final Controller controller = controllerMapper.findController(request);
        if (controller == null) {
            return;
        }
        controller.service(request, response);
    }
}
