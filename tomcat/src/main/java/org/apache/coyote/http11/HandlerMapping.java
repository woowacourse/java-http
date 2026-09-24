package org.apache.coyote.http11;

import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.controller.Controller;

public class HandlerMapping {

    private final Map<String, Controller> requestControllerMap = Map.of(
        "/index", new IndexController(),
        "/login", new LoginController(),
        "/register", new RegisterController()
    );

    public Optional<Controller> getController(final HttpRequest request) {
        final String path = request.path();
        return Optional.ofNullable(requestControllerMap.get(path));
    }

}
