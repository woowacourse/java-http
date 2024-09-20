package org.apache.catalina;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.coyote.processor.ControllerMapper;

public class ServletContainer {

    private final ControllerMapper controllerMapper;

    public ServletContainer(ControllerMapper controllerMapper) {
        this.controllerMapper = controllerMapper;
    }

    public void run(HttpRequest httpRequest, HttpResponse httpResponse) {
        Controller controller = controllerMapper.getController(httpRequest.getPath());
        controller.service(httpRequest, httpResponse);
    }
}
