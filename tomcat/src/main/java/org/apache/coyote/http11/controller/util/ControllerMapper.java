package org.apache.coyote.http11.controller.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.IndexController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.LogoutController;
import org.apache.coyote.http11.controller.RegisterController;

public class ControllerMapper {

    private static final Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/index.html", new IndexController());
        CONTROLLERS.put("/register", new RegisterController());
        CONTROLLERS.put("/login", new LoginController());
        CONTROLLERS.put("/login.html", new LoginController());
        CONTROLLERS.put("/logout", new LogoutController());
    }

    public static Controller getController(final String path) {
        return CONTROLLERS.getOrDefault(path, null);
    }
}
