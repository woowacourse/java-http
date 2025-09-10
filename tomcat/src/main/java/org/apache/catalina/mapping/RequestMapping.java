package org.apache.catalina.mapping;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.core.LoginController;
import org.apache.catalina.core.RegisterController;
import org.apache.catalina.core.RootController;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public static Controller getController(String path) {
        return controllers.get(path);
    }
}
