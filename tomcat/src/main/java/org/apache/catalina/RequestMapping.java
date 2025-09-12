package org.apache.catalina;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticRequestController;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    private static final Controller STATIC_REQUEST_CONTROLLER = new StaticRequestController();
    private static final List<Controller> CONTROLLERS = new ArrayList<>(List.of(STATIC_REQUEST_CONTROLLER));

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public RequestMapping addApplicationController(final Controller controller) {
        CONTROLLERS.add(controller);
        return INSTANCE;
    }

    public Controller getController(final HttpRequest request) {
        for (Controller controller : CONTROLLERS) {
            if (controller.support(request)) {
                return controller;
            }
        }
        throw new NotFoundException();
    }

    private RequestMapping() {
    }
}
