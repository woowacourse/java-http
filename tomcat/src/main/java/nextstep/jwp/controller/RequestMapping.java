package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpRequest;
import org.apache.controller.Controller;

public class RequestMapping {

    public static final String STATIC_FILE_CONTROLLER_KEY = "staticFileController";

    private static final Map<String, Controller> controllers = new HashMap<>();

    private RequestMapping() {
    }

    static {
        controllers.put("/", new HelloWorldController());
        controllers.put("/register", new RegisterController());
        controllers.put("/login", new LoginController());
        controllers.put(STATIC_FILE_CONTROLLER_KEY, new StaticFileController());
    }

    public static Controller get(HttpRequest httpRequest) {
        String uri = httpRequest.getUri();
        final Controller controller = controllers.get(uri);
        if (controller == null) {
            return controllers.get(STATIC_FILE_CONTROLLER_KEY);
        }
        return controller;
    }
}
