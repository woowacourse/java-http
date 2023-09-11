package org.apache.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerAdapter {

    private static final Map<String, Controller> mapper = new HashMap<>();

    static {
        mapper.put("/login", new LoginController());
        mapper.put("/register", new RegisterController());
    }

    private ControllerAdapter() {
    }

    public static Controller findController(String path) {
        return mapper.getOrDefault(path, new FileController());
    }
}
