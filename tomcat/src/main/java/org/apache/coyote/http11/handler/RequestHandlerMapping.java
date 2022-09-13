package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HomeController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.UserController;

public class RequestHandlerMapping {

    private static final RequestHandlerMapping instance = new RequestHandlerMapping();
    private static final Map<String, RequestHandler> handlers = new ConcurrentHashMap<>();

    static {
        handlers.put("/", HomeController.getInstance());
        handlers.put("/login", LoginController.getInstance());
        handlers.put("/register", UserController.getInstance());
    }

    private RequestHandlerMapping() {}

    public static RequestHandlerMapping getInstance() {
        return instance;
    }

    public RequestHandler getHandler(final String path) {
        validateHandlerExistence(path);
        return handlers.get(path);
    }

    private void validateHandlerExistence(final String path) {
        if (!handlers.containsKey(path)) {
            throw new UncheckedServletException("Invalid Uri");
        }
    }
}
