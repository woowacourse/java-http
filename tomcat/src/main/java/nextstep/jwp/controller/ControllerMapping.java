package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.function.Function;
import org.apache.coyote.http11.model.request.HttpRequest;

public enum ControllerMapping {

    LOGIN("/login", LoginController::new),
    REGISTER("/register", RegisterController::new),
    INDEX("/index.html", IndexController::new),
    HOME("/", HomeController::new),
    ;

    private final String path;
    private final Function<HttpRequest, Controller> expression;

    ControllerMapping(final String path, final Function<HttpRequest, Controller> expression) {
        this.path = path;
        this.expression = expression;
    }

    public static Controller findController(final HttpRequest httpRequest) {
        return Arrays.stream(values())
                .filter(handler -> httpRequest.matchTarget(handler.path))
                .findAny()
                .map(mapToController(httpRequest))
                .orElseGet(() -> new ResourceController(httpRequest));
    }

    private static Function<ControllerMapping, Controller> mapToController(final HttpRequest httpRequest) {
        return controllerMapping -> controllerMapping.expression
                .apply(httpRequest);
    }
}
