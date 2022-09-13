package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new RootController());
        controllers.put("/index.html", new RootController());
        controllers.put("/login.html", new LoginController());
        controllers.put("/register.html", new RegisterController());
    }

    public static Controller getController(HttpRequest request) {
        final String requestPath = request.getRequestPath();
        return controllers.keySet()
                .stream()
                .filter(path -> path.equals(requestPath))
                .map(controllers::get)
                .findAny()
                .orElse(new ResourceController());
    }
}
