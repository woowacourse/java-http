package nextstep.jwp.framework.infrastructure.mapping;

import java.util.List;
import nextstep.jwp.framework.infrastructure.exception.NotFoundException;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class HttpRequestMapping implements RequestMapping {

    private final List<Controller> controllers;

    public HttpRequestMapping(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllers.stream()
            .filter(controller -> controller.canProcess(httpRequest))
            .findAny()
            .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
    }
}
