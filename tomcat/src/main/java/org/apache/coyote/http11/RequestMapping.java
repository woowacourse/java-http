package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.PageController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RootController;

public class RequestMapping {

    private static final Map<String, Controller> REQUEST_MAPPER = new HashMap<>();

    static {
        REQUEST_MAPPER.put("/", new RootController());
        REQUEST_MAPPER.put("/login", new LoginController());
        REQUEST_MAPPER.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(final String path) {
        return REQUEST_MAPPER.getOrDefault(path, new PageController());
    }
}
