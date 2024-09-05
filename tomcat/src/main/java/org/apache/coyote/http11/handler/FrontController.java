package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    public FrontController() {
        handlers.put("/", new HomeHandler());
        handlers.put("/index", new IndexHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new RegisterHandler());
    }

    public void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        RequestHandler handler = handlers.get(request.getPath());
        log.debug("Request Line: {}", request.getRequestLine());
        log.debug("Body: {}", request.getParams());

        if (handler != null) {
            handler.handle(request, response);
            return;
        }

        new StaticResourceHandler().handle(request, response);
    }
}
