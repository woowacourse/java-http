package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.request.HttpRequest;

public class ControllerContainer {

    private static final List<Controller> controllers = Arrays.asList(
        new LoginController(),
        new PageRenderController(),
        new RegisterController(),
        new StaticFileController()
    );

    public static Controller findController(HttpRequest httpRequest) {
        return controllers.stream()
            .filter(it -> it.isMatchingController(httpRequest))
            .findAny()
            .orElseGet(StaticFileController::new);
    }
}
