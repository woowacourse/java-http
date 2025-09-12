package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;

public class ControllerMapper {

    private static final Map<String, Controller> pathMappings = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private static final ControllerMapper INSTANCE = new ControllerMapper();

    public Controller getController(final String path) {
        return pathMappings.getOrDefault(path, new AbstractController());
    }

    public static ControllerMapper getInstance() {
        return INSTANCE;
    }
}
