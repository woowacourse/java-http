package org.apache.coyote.http11.router;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.StaticFileHandler;

public class Router {

    private final StaticFileHandler staticFileHandler;
    private final Map<String, Handler> handlers;

    public Router(final StaticFileHandler staticFileHandler) {
        this.staticFileHandler = staticFileHandler;
        this.handlers = new HashMap<>();
        registerHandlers();
    }

    public Handler route(final HttpRequest request) {
        if (handlers.containsKey(request.route())) {
            return handlers.get(request.route());
        }
        return staticFileHandler;
    }

    private void registerHandlers() {
        handlers.put("/login", new LoginHandler(staticFileHandler));
        handlers.put("/register", new RegisterHandler(staticFileHandler));
    }
}
