package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.*;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapping {
    private static final List<Controller> CONTROLLER_INSTANCES = List.of(
            new LoginGetController(), new LoginPostController(), new RegisterGetController(), new RegisterPostController(), new DefaultGetController()
    );

    private RequestMapping() {
    }

    public static Controller getController(HttpRequest request) {
        return CONTROLLER_INSTANCES.stream()
                .filter(controller -> controller.isSupported(request))
                .findFirst()
                .orElse(new DefaultController());
    }
}

