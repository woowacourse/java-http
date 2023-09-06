package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> servletMap = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private static final Controller DEFAULT_SERVLET = new ResourceController();

    public Controller getController(final HttpRequest request) {
        String path = request.getPath();
        return servletMap.getOrDefault(path, DEFAULT_SERVLET);
    }
}
