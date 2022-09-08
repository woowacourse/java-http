package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.session.SessionManager;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = new ConcurrentHashMap<>();

    public RequestMapping() {
    }

    static {
        CONTROLLERS.put("/", new RootController());
        CONTROLLERS.put("/login", new LoginController(new SessionManager()));
        CONTROLLERS.put("/register", new RegisterController());
        CONTROLLERS.put("static", new StaticFileController());
    }

    public Controller getController(HttpRequest request) {
        if (!CONTROLLERS.containsKey(request.getUrl())) {
            return CONTROLLERS.get("static");
        }
        return CONTROLLERS.get(request.getUrl());
    }
}
