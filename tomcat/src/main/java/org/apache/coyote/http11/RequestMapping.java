package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.SignupController;
import com.techcourse.controller.StaticController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private static final Map<String, Controller> controllerMap = new ConcurrentHashMap<>();

    static {
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new SignupController());
        controllerMap.put("/", new StaticController());
    }

    public static Controller getController(HttpRequest request) {
        String path = request.getPath();
        Controller controller = controllerMap.get(path);
        if (controller == null) {
            return controllerMap.get("/");
        }
        return controller;
    }
}
