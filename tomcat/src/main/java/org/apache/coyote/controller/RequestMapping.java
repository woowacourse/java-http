package org.apache.coyote.controller;

import java.util.List;
import org.apache.coyote.domain.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapping {

    private static final Logger log = LoggerFactory.getLogger(RequestMapping.class);
    private static final List<Controller> controllers = List.of(new LoginController());

    public Controller getController(HttpRequest httpRequest) {
        return controllers.stream()
                .filter(controller -> controller.handle(httpRequest))
                .findFirst()
                .orElse(new StaticFileController());
    }
}
