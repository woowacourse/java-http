package org.apache.catalina.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.ViewController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllerMapping = new HashMap<>();

    public RequestMapping() {
        controllerMapping.put("/login", new LoginController());
        controllerMapping.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String path = request.getRequestLine().getPath();
        Controller controller = controllerMapping.get(path);
        if (controller == null) {
            return new ViewController();
        }

        return controller;
    }
}

