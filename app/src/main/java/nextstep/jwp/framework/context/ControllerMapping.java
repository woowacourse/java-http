package nextstep.jwp.framework.context;

import java.util.ArrayList;
import java.util.List;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.webserver.controller.Controller;
import nextstep.jwp.webserver.controller.ErrorController;
import nextstep.jwp.webserver.controller.IndexPageController;
import nextstep.jwp.webserver.controller.WelcomePageController;

public class ControllerMapping {

    private static final List<Controller> CONTROLLERS = new ArrayList<>();

    static {
        CONTROLLERS.add(new WelcomePageController());
        CONTROLLERS.add(new IndexPageController());
        CONTROLLERS.add(new ErrorController());
    }

    public static Controller findController(HttpRequest httpRequest) {
        return CONTROLLERS.stream()
                          .filter(controller -> controller.canHandle(httpRequest))
                          .findAny()
                          .orElse(ErrorController.INSTANCE);
    }
}
