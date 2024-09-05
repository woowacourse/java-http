package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    public FrontController() {
        handlers.put("/", new IndexHandler());
    }

    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestHandler handler = handlers.get(httpRequest.getPath());
        if (handler != null) {
            handler.handle(httpRequest, httpResponse);
        }

        new NotFoundHandler().handle(httpRequest, httpResponse);
    }
}
