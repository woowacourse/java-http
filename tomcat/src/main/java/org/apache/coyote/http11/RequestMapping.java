package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private final Map<String, Controller> mappings = new HashMap<>();
    private final Controller defaultController = new StaticFileController();

    public RequestMapping() {
        mappings.put("/", new RootController());
        mappings.put("/login", new LoginController());
        mappings.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        return mappings.getOrDefault(request.getPath(), defaultController);
    }
}
