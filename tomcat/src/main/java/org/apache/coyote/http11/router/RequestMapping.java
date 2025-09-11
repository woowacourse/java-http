package org.apache.coyote.http11.router;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public final class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();
    private final Map<String, Controller> controllerMap = new HashMap<>();

    private RequestMapping() {
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public Optional<Controller> getController(HttpRequest request) {
        return Optional.ofNullable(controllerMap.get(request.getUrl()));
    }
}
