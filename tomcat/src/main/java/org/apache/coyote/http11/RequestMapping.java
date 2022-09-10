package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.ResourceHandler;

public class RequestMapping {

    private static final Map<String, Controller> requestMap = new HashMap<>();

    static {
        requestMap.put("/", new HomeController());
        requestMap.put("/login", new LoginController());
        requestMap.put("/register", new RegisterController());
    }

    public static Controller find(final String requestUri) {
        Controller controller = requestMap.get(requestUri);
        if (controller == null) {
            return new ResourceHandler();
        }
        return controller;
    }
}
