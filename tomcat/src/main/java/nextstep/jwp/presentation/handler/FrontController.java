package nextstep.jwp.presentation.handler;

import nextstep.jwp.presentation.*;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.apache.coyote.http.HttpMethod.POST;

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

    public String process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod method = httpRequest.getMethod();
        String path = httpRequest.getPath();
        Controller controller = findController(method, path);

        return controller.process(httpRequest, httpResponse);
    }

    public Controller findController(HttpMethod method, String path) {
        if (isStaticPath(path)) {
            return STATIC_CONTROLLER;
        }
        if (method == GET && getMappingControllers.containsKey(path)) {
            return getMappingControllers.get(path);
        }
        if (method == POST && postMappingControllers.containsKey(path)) {
            return postMappingControllers.get(path);
        }
        return NOT_FOUND_CONTROLLER;
    }

    private boolean isStaticPath(String path) {
        return STATIC_PATH.stream().anyMatch(path::endsWith);
    }
}
