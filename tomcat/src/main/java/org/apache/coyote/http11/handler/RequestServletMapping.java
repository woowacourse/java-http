package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginServlet;
import nextstep.jwp.handler.RegisterServlet;

public class RequestServletMapping {

    private static final Map<String, RequestServlet> handlers = new ConcurrentHashMap<>();

    public static RequestServletMapping init() {
        handlers.put("/login", new LoginServlet());
        handlers.put("/register", new RegisterServlet());

        return new RequestServletMapping();
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
