package nextstep.jwp.presentation.handler;

import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.NotFoundController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.RootController;
import nextstep.jwp.presentation.StaticController;
import org.apache.coyote.http.HttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontController {

    private static final List<String> STATIC_PATH = List.of(".css", ".js", ".ico", ".html", ".svg");
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();
    private static final Controller STATIC_CONTROLLER = new StaticController();

    private final Map<String, Controller> mappingControllers = new HashMap<>();

    public FrontController() {
        mappingControllers.put("/", new RootController());
        mappingControllers.put("/login", new LoginController());
        mappingControllers.put("/register", new RegisterController());
    }

    public Controller handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (isStaticPath(path)) {
            return STATIC_CONTROLLER;
        }
        if (mappingControllers.containsKey(path)) {
            return mappingControllers.get(path);
        }
        return NOT_FOUND_CONTROLLER;
    }

    private boolean isStaticPath(String path) {
        return STATIC_PATH.stream().anyMatch(path::endsWith);
    }
}
