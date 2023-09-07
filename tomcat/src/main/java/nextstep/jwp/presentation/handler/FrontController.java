package nextstep.jwp.presentation.handler;

import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.GetLoginController;
import nextstep.jwp.presentation.GetRegisterController;
import nextstep.jwp.presentation.NotFoundController;
import nextstep.jwp.presentation.PostLoginController;
import nextstep.jwp.presentation.PostRegisterController;
import nextstep.jwp.presentation.RootController;
import nextstep.jwp.presentation.StaticController;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontController {

    private static final List<String> STATIC_PATH = List.of(".css", ".js", ".ico", ".html", ".svg");
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();
    private static final Controller STATIC_CONTROLLER = new StaticController();

    private final Map<String, Controller> getMappingControllers = new HashMap<>();
    private final Map<String, Controller> postMappingControllers = new HashMap<>();

    public FrontController() {
        getMappingControllers.put("/", new RootController());
        getMappingControllers.put("/login", new GetLoginController());
        getMappingControllers.put("/register", new GetRegisterController());
        postMappingControllers.put("/login", new PostLoginController());
        postMappingControllers.put("/register", new PostRegisterController());
    }

    public String process(HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) throws IOException {
        String method = httpRequestParser.getMethod();
        String path = httpRequestParser.getPath();
        Controller controller = findController(method, path);

        return controller.process(httpRequestParser, httpResponseBuilder);
    }

    public Controller findController(String method, String path) {
        if (isStaticPath(path)) {
            return STATIC_CONTROLLER;
        }
        if (method.equals("GET") && getMappingControllers.containsKey(path)) {
            return getMappingControllers.get(path);
        }
        if (method.equals("POST") && postMappingControllers.containsKey(path)) {
            return postMappingControllers.get(path);
        }
        return NOT_FOUND_CONTROLLER;
    }

    private boolean isStaticPath(String path) {
        return STATIC_PATH.stream().anyMatch(path::endsWith);
    }
}
