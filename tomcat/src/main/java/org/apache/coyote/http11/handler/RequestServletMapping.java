package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HomeServlet;
import nextstep.jwp.handler.LoginServlet;
import nextstep.jwp.handler.UserServlet;

public class RequestServletMapping {

    private static final Map<String, RequestServlet> handlers = new ConcurrentHashMap<>();

    static {
        handlers.put("/", new HomeServlet());
        handlers.put("/login", new LoginServlet());
        handlers.put("/register", new UserServlet());
    }

    public RequestServlet getHandler(final String path) {
        validateHandlerExistence(path);
        return handlers.get(path);
    }

    public void validateHandlerExistence(final String path) {
        if (!handlers.containsKey(path)) {
            throw new UncheckedServletException("Invalid Uri");
        }
    }
}
