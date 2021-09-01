package nextstep.jwp.framework.infrastructure.mapping;

import java.util.List;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.infrastructure.exception.NotFoundException;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;

public class HttpRequestMapping implements RequestMapping {

    private final List<Controller> customControllers;
    private final List<Controller> standardControllers;

    public HttpRequestMapping(
        List<Controller> customControllers,
        List<Controller> standardControllers
    ) {
        this.customControllers = customControllers;
        this.standardControllers = standardControllers;
    }

    public Controller findController(HttpRequest httpRequest) {
        return customControllers.stream()
            .filter(controller -> controller.canProcess(httpRequest))
            .findAny()
            .orElseGet(() -> findStandardControllers(httpRequest));
    }

    private Controller findStandardControllers(HttpRequest httpRequest) {
        return standardControllers.stream()
            .filter(controller -> controller.canProcess(httpRequest))
            .findAny()
            .orElseThrow(NotFoundException::new);
    }
}
