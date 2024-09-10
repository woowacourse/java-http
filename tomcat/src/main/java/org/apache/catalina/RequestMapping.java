package org.apache.catalina;

import com.techcourse.controller.HelloController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Controller;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private final Controller staticResourceController = new StaticResourceController();

    static {
        controllers.put("/", new HelloController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        Controller controller = controllers.get(request.getPath());
        if (controller == null) {
            return staticResourceController;
        }
        return controller;
    }
}
