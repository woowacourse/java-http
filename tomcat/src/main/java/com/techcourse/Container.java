package com.techcourse;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import java.util.Map;

public class Container {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller mapController(String mappingUri) {
        return controllers.get(mappingUri);
    }
}
