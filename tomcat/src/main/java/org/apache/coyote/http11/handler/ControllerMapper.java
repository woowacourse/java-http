package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.StaticFileController;
import org.apache.coyote.http11.message.request.RequestLine;

public class ControllerMapper {

    private static final Controller DEFAULT_CONTROLLER = new StaticFileController();
    private final Map<HandlerStatus, Controller> handlers = new HashMap<>();

    public ControllerMapper() {
    }

    public void addController(HandlerStatus handlerStatus, Controller controller) {
        handlers.put(handlerStatus, controller);
    }

    public Controller findHandler(RequestLine requestLine) {
        return handlers.getOrDefault(HandlerStatus.from(requestLine), DEFAULT_CONTROLLER);
    }
}
