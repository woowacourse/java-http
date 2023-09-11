package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.PageController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;

public class RequestMapping {

    private static final Map<String, Controller> REQUEST_MAPPER = new HashMap<>();

    static {
        REQUEST_MAPPER.put("/", new RootController());
        REQUEST_MAPPER.put("/login", new LoginController());
        REQUEST_MAPPER.put("/register", new RegisterController());
    }

    public static Controller getController(final String path) {
        if (REQUEST_MAPPER.containsKey(path)) {
            return REQUEST_MAPPER.get(path);
        }
        return new PageController();
    }

}
