package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.handler.Controller;
import nextstep.jwp.handler.DefaultController;
import nextstep.jwp.handler.FileController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;

public class RequestMapping {

    private static final String FILE_CONTROLLER_KEY = "file";
    private static final Manager MANAGER = new SessionManager();
    private static final Map<String, Controller> values = new HashMap<>();

    static {
        values.put("/", new DefaultController());
        values.put("/login", new LoginController(MANAGER));
        values.put("/register", new RegisterController());
        values.put(FILE_CONTROLLER_KEY, new FileController());
    }

    private RequestMapping() {
    }

    public static Controller of(final String url) {
        Controller controller = values.get(url);
        if (Objects.isNull(controller)) {
            return values.get(FILE_CONTROLLER_KEY);
        }
        return controller;
    }
}
