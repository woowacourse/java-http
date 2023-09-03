package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerAdapter {

    private static final Map<String, Controller> map = new ConcurrentHashMap<>();
    private static final String STATIC_CONTROLLER = "staticController";
    private static final String ERROR_CONTROLLER = "errorController";

    public ControllerAdapter() {
        init();
    }

    private void init() {
        map.put(STATIC_CONTROLLER, new StaticController());
        map.put(ERROR_CONTROLLER, new ErrorController());
        map.put("/login", new LoginController());
        map.put("/register", new RegisterController());
    }

    public Controller findController(HttpRequest httpRequest) {
        if (httpRequest.isStaticRequest()) {
            return map.get(STATIC_CONTROLLER);
        }
        if (map.containsKey(httpRequest.getUri())) {
            return map.get(httpRequest.getUri());
        }
        return map.get(ERROR_CONTROLLER);
    }
}
