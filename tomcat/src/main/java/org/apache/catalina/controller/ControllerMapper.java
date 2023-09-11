package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.StaticFileController;
import org.apache.coyote.http11.message.request.RequestLine;

public class ControllerMapper {

    private static final Controller DEFAULT_CONTROLLER = new StaticFileController();
    private final Map<ControllerStatus, Controller> handlers = new HashMap<>();

    public ControllerMapper() {
    }

    public void addController(final ControllerStatus controllerStatus, final Controller controller) {
        handlers.put(controllerStatus, controller);
    }

    public Controller findHandler(final RequestLine requestLine) {
        return handlers.getOrDefault(ControllerStatus.from(requestLine), DEFAULT_CONTROLLER);
    }
}
