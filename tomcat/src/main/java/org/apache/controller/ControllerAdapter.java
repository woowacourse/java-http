package org.apache.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerAdapter {

    private static final Map<String, Controller> mapper = new HashMap<>();

    static {
        mapper.put("/index.html", new IndexController());
        mapper.put("/login", new LoginController());
        mapper.put("/register", new RegisterController());
        mapper.put("/css/styles.css", new CssController());
        mapper.put("/js/scripts.js", new JsController());
        mapper.put("/assets/chart-area.js", new JsController());
        mapper.put("/assets/chart-bar.js", new JsController());
        mapper.put("/assets/chart-pie.js", new JsController());
        mapper.put("/401.html", new UnAuthorizedController());
    }

    private ControllerAdapter() {
    }

    public static Controller findController(String path) {
        return mapper.getOrDefault(path, new NotFoundController());
    }
}
