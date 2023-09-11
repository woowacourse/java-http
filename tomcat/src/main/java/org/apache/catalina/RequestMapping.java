package org.apache.catalina;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Handler staticHandler = new StaticFileHandler();

    private final Map<String, Handler> handlers = new HashMap<>();

    public RequestMapping addHandler(String uri, Handler handler) {
        handlers.put(uri, handler);
        return this;
    }

    public Handler getHandler(final HttpRequest httpRequest) {
        String requestURI = httpRequest.getRequestLine().getRequestURI();
        return handlers.getOrDefault(requestURI, staticHandler);
    }
}
