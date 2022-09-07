package org.apache.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new HelloController());
        controllers.put("/index", new FileController());
        controllers.put("/register", new UserController());
        controllers.put("/login", new AuthController());
    }

    public static Controller findController(String requestUrl) {
        if (controllers.containsKey(requestUrl)) {
            return controllers.get(requestUrl);
        }
        return new FileController();
    }
}
