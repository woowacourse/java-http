package org.apache.coyote.http11.requestmapping;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootApiController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class ApiHandlerMapper implements RequestMapper {

    private static final Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/", new RootApiController());
        CONTROLLERS.put("/login", new LoginController());
        CONTROLLERS.put("/register", new RegisterController());
    }

    public Controller mapController(HttpRequest httpRequest) {
        return CONTROLLERS.get(httpRequest.getPath());
    }
}

