package org.apache.coyote.support;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.UserController;
import org.apache.coyote.http11.http.HttpPath;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new DefaultController());
        controllers.put("/index", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new UserController());
        controllers.put("", new ResourceController());
    }

    public Controller getController(final HttpPath path) {
        String value = path.getValue();
        if (value.equals("/")) {
            return controllers.get("/");
        }
        String[] split = value.split("\\.");
        Controller controller = controllers.get(split[0]);
        if (controller == null) {
            return controllers.get("");
        }
        return controller;
    }
}