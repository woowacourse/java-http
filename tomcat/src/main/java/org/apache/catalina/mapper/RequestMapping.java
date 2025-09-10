package org.apache.catalina.mapper;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.request.ServletRequest;

public class RequestMapping {

    private static final Map<String, Controller> MAPPINGS;
    private static final DefaultController DEFAULT_CONTROLLER;

    static {
        final Map<String, Controller> mappings = new HashMap<>();
        mappings.put("/", new RootController());
        mappings.put("/login", new LoginController());
        mappings.put("/register", new RegisterController());

        MAPPINGS = Collections.unmodifiableMap(mappings);
        DEFAULT_CONTROLLER = new DefaultController();
    }

    public Controller getControllerOrDefault(ServletRequest request) {
        return MAPPINGS.getOrDefault(request.getPath().getValue(), DEFAULT_CONTROLLER);
    }
}
