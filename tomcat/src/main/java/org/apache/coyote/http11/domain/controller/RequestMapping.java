package org.apache.coyote.http11.domain.controller;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.service.LoginService;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.domain.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller resourceController;

    public RequestMapping() {
        this.controllers = new HashMap<>();
        this.resourceController = new ResourceController();

        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController(new LoginService()));
        controllers.put("/register", new RegisterController());
    }

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers = controllers;
        this.resourceController = new ResourceController();
    }

    public Controller getController(HttpRequest request) {
        Controller controller = controllers.get(request.getPath());
        if (controller == null) {
            return resourceController;
        }
        return controller;
    }

    public boolean canHandle(HttpRequest request) {
        return controllers.containsKey(request.getPath());
    }
}

