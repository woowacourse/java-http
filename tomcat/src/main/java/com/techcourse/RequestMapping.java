package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        return controllers.get(request.getPath());
    }
}
