package org.apache.catalina;

import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ExceptionHandler;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RequestMapping {

    private final List<Controller> controllers;
    private final ExceptionHandler exceptionHandler;

    RequestMapping(List<Controller> controllers, ExceptionHandler exceptionHandler) {
        this.controllers = controllers;
        this.exceptionHandler = exceptionHandler;
    }

    public static RequestMapping init() {
        return new RequestMapping(
                List.of(
                        new HomeController(),
                        new LoginController(),
                        new RegisterController(),
                        new ResourceController()
                ),
                new ExceptionHandler()
        );
    }

    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            mapAndProcess(httpRequest, httpResponse);
            validateResponse(httpResponse);
        } catch (UncheckedServletException e) {
            exceptionHandler.handle(httpResponse, e);
        }
    }

    private void mapAndProcess(HttpRequest httpRequest, HttpResponse httpResponse) {
        controllers.stream()
                .filter(controller -> controller.canProcess(httpRequest))
                .findAny()
                .ifPresent(controller -> controller.service(httpRequest, httpResponse));
    }

    private void validateResponse(HttpResponse httpResponse) {
        if (!httpResponse.isDetermined()) {
            throw new NotFoundException();
        }
    }
}
