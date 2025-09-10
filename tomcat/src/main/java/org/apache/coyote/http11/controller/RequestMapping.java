package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {
    private static final Map<String, Controller> GET = new HashMap<>();
    private static final Map<String, Controller> POST = new HashMap<>();
    private static final Controller ROOT = (req, res) -> res.ok("Hello world!", "text/html;charset=utf-8");
    private static final Controller STATIC_RESOURCE_CONTROLLER = new StaticResourceController();

    static {
        GET.put("/", ROOT);
        GET.put("/login", new LoginController());
        GET.put("/register", new RegisterController());
        POST.put("/login", new LoginController());
        POST.put("/register", new RegisterController());
    }

    public static Controller getController(HttpRequest request) {
        String method = request.getMethod();
        String path = request.getPath();

        Map<String, Controller> table = getMethodControllers(method);
        return table.getOrDefault(path, STATIC_RESOURCE_CONTROLLER);
    }

    private static Map<String, Controller> getMethodControllers(String method) {
        if("GET".equals(method)) {
            return GET;
        }
        if("POST".equals(method)) {
            return POST;
        }
        return new HashMap<>();
    }
}
