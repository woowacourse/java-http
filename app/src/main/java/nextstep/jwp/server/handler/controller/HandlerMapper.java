package nextstep.jwp.server.handler.controller;

import nextstep.jwp.http.exception.NotFoundException;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.server.handler.controller.static_files.StaticFilesController;

import java.util.List;

public class HandlerMapper {

    private final List<Controller> customControllers;
    private final List<StaticFilesController> staticFilesControllers;

    public HandlerMapper(List<Controller> customControllers, List<StaticFilesController> staticFilesControllers) {
        this.customControllers = customControllers;
        this.staticFilesControllers = staticFilesControllers;
    }

    public Controller findController(HttpRequest httpRequest) {
        Controller controller = customControllers.stream()
                .filter(customController -> customController.isSatisfiedBy(httpRequest))
                .findAny()
                .orElseGet(() -> findStandardController(httpRequest));

        return new StandardController(controller);
    }

    private StaticFilesController findStandardController(HttpRequest httpRequest) {
        return staticFilesControllers.stream()
                .filter(controller -> controller.isSatisfiedBy(httpRequest))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }
}
