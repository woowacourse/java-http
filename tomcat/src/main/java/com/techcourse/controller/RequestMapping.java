package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final GreetingController greetingController = new GreetingController();
    private static final LoginController loginController = new LoginController();
    private static final RegisterController registerController = new RegisterController();

    private static final Map<String, Controller> controllers = Map.of(
            "/", greetingController,
            "/login", loginController,
            "/register", registerController
    );

    public static Controller getController(HttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getPath();

        if (!controllers.containsKey(path)) {
            throw new IllegalArgumentException("Handler not found for " + method + " " + path);
        }
        return controllers.get(path);
    }
}
