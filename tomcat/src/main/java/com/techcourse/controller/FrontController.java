package com.techcourse.controller;

import com.techcourse.web.WebApplication;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FrontController implements WebApplication {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController;

    public FrontController() {
        this.defaultController = new DefaultController();
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public HttpResponse service(HttpRequest request) {
        String requestPath = request.getPath();
        
        Controller controller = findController(requestPath);
        return controller.service(request);
    }

    private Controller findController(String requestPath) {
        if (controllers.containsKey(requestPath)) {
            return controllers.get(requestPath);
        }

        if (Objects.equals("/", requestPath)) {
            return controllers.get("/");
        }

        return defaultController;
    }
}
