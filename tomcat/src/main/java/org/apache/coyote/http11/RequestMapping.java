package org.apache.coyote.http11;

import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.StaticRequestController;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    private static final Controller STATIC_REQUEST_CONTROLLER = new StaticRequestController();
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();
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
        return NOT_FOUND_CONTROLLER;
    }

    private RequestMapping() {
    }
}
