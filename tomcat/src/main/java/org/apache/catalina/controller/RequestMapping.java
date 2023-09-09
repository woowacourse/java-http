package org.apache.catalina.controller;

import nextstep.jwp.presentation.HomePageController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.LoginPageController;
import nextstep.jwp.presentation.RegisterPageController;
import nextstep.jwp.presentation.ResourceController;
import org.apache.coyote.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.POST;

public class RequestMapping {

    private static final ResourceController DEFAULT_CONTROLLER = new ResourceController();
    private static final Map<ControllerMappingInfo, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put(ControllerMappingInfo.of(GET, false, "/"), new HomePageController());

        CONTROLLERS.put(ControllerMappingInfo.of(GET, false, "/login"), new LoginPageController());
        CONTROLLERS.put(ControllerMappingInfo.of(GET, true, "/login"), new LoginController());

        CONTROLLERS.put(ControllerMappingInfo.of(GET, false, "/register"), new RegisterPageController());
        CONTROLLERS.put(ControllerMappingInfo.of(POST, false, "/register"), new RegisterPageController());
    }

    private RequestMapping() {
    }

    public static Controller getController(final HttpRequest request) {
        return CONTROLLERS.getOrDefault( ControllerMappingInfo.from(request), DEFAULT_CONTROLLER);
    }
}
