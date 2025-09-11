package org.apache.coyote.http11.mapper;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.controller.StaticResourceController;

public class RequestMapping {

    private final Map<String, Controller> mapper = new HashMap<String, Controller>();

    public RequestMapping() {
        mapper.put("/", new RootController());
        mapper.put("/login", new LoginController());
        mapper.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String path = request.getUri().getPath();
        return mapper.getOrDefault(path, new StaticResourceController());
    }
}
