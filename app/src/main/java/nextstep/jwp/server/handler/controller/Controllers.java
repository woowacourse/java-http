package nextstep.jwp.server.handler.controller;

import nextstep.jwp.http.exception.NotFoundException;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.server.handler.controller.standard.StandardController;

import java.util.List;

public class Controllers {

    private final List<Controller> customControllers;
    private final List<StandardController> standardControllers;

    public Controllers(List<Controller> customControllers, List<StandardController> standardControllers) {
        this.customControllers = customControllers;
        this.standardControllers = standardControllers;
    }

    public Controller findController(HttpRequest httpRequest) {
        return customControllers.stream()
                .filter(controller -> controller.isSatisfiedBy(httpRequest))
                .findAny()
                .orElseGet(() -> findStandardController(httpRequest));
    }

    private StandardController findStandardController(HttpRequest httpRequest) {
        return standardControllers.stream()
                .filter(controller -> controller.isSatisfiedBy(httpRequest))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }
}
