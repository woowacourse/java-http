package nextstep.jwp;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.commandcontroller.Controller;
import nextstep.jwp.request.HttpRequest;

public class RequestMapping {

    private final List<Controller> controllers = new ArrayList<>();
    private Controller defaultController;

    public RequestMapping(final List<Controller> controllers) {
        this.controllers.addAll(controllers);
    }

    public void setDefaultController(final Controller controller) {
        this.defaultController = controller;
    }

    public Controller getController(final HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(request))
                .findAny()
                .orElse(defaultController);
    }
}
