package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;

public class RequestMapping {
    private final Controller staticController = new StaticResourceController();

    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticController);
    }
}
