package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    public FrontController() {
        handlers.put("/", new HomeHandler());
        handlers.put("/index", new IndexHandler());
        handlers.put("/login", new LoginHandler());
    }

    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        RequestHandler handler = handlers.get(httpRequest.getPath());
        if (handler != null) {
            handler.handle(httpRequest, httpResponse);
            return;
        }

        new StaticResourceHandler().handle(httpRequest, httpResponse);
    }
}
