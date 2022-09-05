package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.LoginController;

public class RequestMapping {

    private static final Map<String, Controller> requestMap = new HashMap<>();

    static {
        requestMap.put("/", new HomeController());
        requestMap.put("/login", new LoginController());
    }

    public static Controller find(final String requestUri) {
        Controller controller = requestMap.get(requestUri);
        if (controller == null) {
            return new ResourceHandler();
        }
        return controller;
    }
}
