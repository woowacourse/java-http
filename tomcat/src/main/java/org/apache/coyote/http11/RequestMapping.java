package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    
    private final Map<String, Controller> mappings = new HashMap<>();
    private final Controller defaultController = new StaticFileController();

    public RequestMapping(Session session) {
        mappings.put("/login", new LoginController(session));
        mappings.put("/register", new RegisterController());
    }

    public Controller getController(String path) {
        return mappings.getOrDefault(path, defaultController);
    }
}
