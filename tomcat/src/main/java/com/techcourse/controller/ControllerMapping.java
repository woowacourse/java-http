package com.techcourse.controller;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.DefaultController;
import org.apache.catalina.HandlerMapping;
import org.apache.http.request.HttpRequest;

public class ControllerMapping implements HandlerMapping {
    private static final ControllerMapping INSTANCE = new ControllerMapping();
    private static final String PATH_DELIMITER = "/";

    private ControllerMapping() {
    }

    public static ControllerMapping getInstance() {
        return INSTANCE;
    }

    public Controller getHandler(final HttpRequest httpRequest) {
        return getHandlerByEndPoint(httpRequest);
    }

    private Controller getHandlerByEndPoint(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();

        if (path.equals(PATH_DELIMITER)) {
            return RootEndPointController.getInstance();
        }

        if (path.equals("/login")) {
            return LoginController.getInstance();
        }

        if (path.equals("/register")) {
            return RegisterController.getInstance();
        }

        return DefaultController.getInstance();
    }
}
