package com.techcourse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import org.apache.coyote.http11.StaticResourceHandler;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        Controller controller = controllers.get(request.getRequestLine().getPath());
        if (controller != null) {
            return controller;
        }

        return (req, res) -> {
            try {
                StaticResourceHandler.handleStaticResource(req.getRequestLine().getPath(), res);
            } catch (IOException e) {
                res.setStatusLine("HTTP/1.1", 500, "Internal Server Error");
                res.addHeader("Content-Type", "text/plain;charset=utf-8");
                res.setBody("Static resource handling failed".getBytes());
            }
        };
    }
}
