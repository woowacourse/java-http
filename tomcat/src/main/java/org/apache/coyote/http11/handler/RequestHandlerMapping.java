package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HomeController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.UserServlet;

public class RequestHandlerMapping {

    private static final Map<String, RequestHandler> handlers = new ConcurrentHashMap<>();

    static {
        handlers.put("/", new HomeController());
        handlers.put("/login", new LoginController());
        handlers.put("/register", new UserServlet());
    }

    public RequestHandler getHandler(final String path) {
        validateHandlerExistence(path);
        return handlers.get(path);
    }

    public void validateHandlerExistence(final String path) {
        if (!handlers.containsKey(path)) {
            throw new UncheckedServletException("Invalid Uri");
        }
    }
}
