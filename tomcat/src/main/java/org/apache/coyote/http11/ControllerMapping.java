package org.apache.coyote.http11;

import javassist.NotFoundException;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.GreetingController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ResourceController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerMapping {

    private static final List<String> resourceSuffixes = List.of(".html", ".css", ".js");
    private final Map<String, Controller> mapping = new HashMap<>();

    public ControllerMapping() {
        mapping.put("/", new GreetingController());
        mapping.put("/login", new LoginController());
    }

    public Controller getController(final String uri) throws NotFoundException {
        final Controller controller = mapping.get(uri);
        if (controller != null) {
            return controller;
        }
        if (isResourceUri(uri)) {
            return new ResourceController();
        }
        throw new NotFoundException(uri + "를 처리할 컨트롤러를 찾지 못함");
    }

    private boolean isResourceUri(final String uri) {
        for (String resourceSuffix : resourceSuffixes) {
            if (uri.endsWith(resourceSuffix)) {
                return true;
            }
        }
        return false;
    }
}
