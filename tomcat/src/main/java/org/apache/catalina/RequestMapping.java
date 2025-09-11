package org.apache.catalina;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> pathMappings = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(HttpRequest request) {
        return pathMappings.get(request.getPath());
    }
}
