package org.apache.coyote.http11.controller;

import java.util.Optional;
import org.apache.coyote.http11.config.ControllerConfig;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public Optional<Controller> getController(HttpRequest request) {
        return ControllerConfig.getControllers().stream()
                .filter(controller -> request.getPath().equals(controller.getPath()))
                .findFirst();
    }
}
