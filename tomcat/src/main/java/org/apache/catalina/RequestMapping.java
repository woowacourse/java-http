package org.apache.catalina;

import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping {

    private final List<Controller> controllers;

    public RequestMapping(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public static RequestMapping init(SessionManager sessionManager) {
        AuthService authService = new AuthService(sessionManager);
        return new RequestMapping(
                List.of(
                        new HomeController(),
                        new LoginController(authService),
                        new RegisterController(authService),
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
