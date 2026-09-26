package org.apache.coyote.http11;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.DefaultController;
import org.apache.catalina.controller.IndexController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RootController;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new RootController(),
            "/index", new IndexController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public static Controller getController(String path) {
        return CONTROLLERS.getOrDefault(path, new DefaultController());
    }
}
