package nextstep.jwp.controller;

import nextstep.jwp.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private static Map<String, Controller> controllers = new ConcurrentHashMap<>();

    private RequestMapping() {
    }

    static {
        controllers.put("/login", ApplicationContext.loginController());
        controllers.put("/register", ApplicationContext.registerController());
    }

    public static Controller getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
