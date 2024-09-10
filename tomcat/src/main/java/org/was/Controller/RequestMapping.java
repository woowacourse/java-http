package org.was.Controller;

import java.util.HashMap;
import java.util.Map;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ViewController;
import org.apache.coyote.http11.request.HttpRequest;
import org.was.Controller.exception.ControllerNotFoundException;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    private final Map<String, Controller> controllerMapping = new HashMap<>();

    private RequestMapping() {
        controllerMapping.put("/login", new LoginController());
        controllerMapping.put("/register", new RegisterController());
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getRequestLine().getPath();
        Controller controller = controllerMapping.get(path);

        if (controller == null) {
            throw new ControllerNotFoundException(path);
        }

        return controller;
    }
}

