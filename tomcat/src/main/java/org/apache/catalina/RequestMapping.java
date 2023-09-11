package org.apache.catalina;

import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RequestMapping {

    private final List<Controller> controllers;

    RequestMapping(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public static RequestMapping init() {
        return new RequestMapping(
                List.of(
                        new HomeController(),
                        new LoginController(),
                        new RegisterController(),
                        new ResourceController()
                )
        );
    }

    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        controllers.stream()
                .filter(controller -> controller.canProcess(httpRequest))
                .findAny()
                .ifPresent(controller -> controller.service(httpRequest, httpResponse));
    }
}
