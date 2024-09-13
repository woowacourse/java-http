package org.apache.catalina;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.HashMap;
import java.util.Map;

public class Container {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("/static-resource", new StaticResourceController());
    }

    private Container() {
    }

    public static Controller mapController(String mappingUri) {
        Controller controller = controllers.get(mappingUri);
        if (controller == null) {
            return controllers.get("/static-resource");
        }
        return controllers.get(mappingUri);
    }
}
