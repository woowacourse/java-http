package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    private static final Map<String, RequestHandler> requestHandlerMap = new HashMap<>();

    static {
        requestHandlerMap.put("/", new HomeHandler());
        requestHandlerMap.put("/login", new LoginHandler());
    }

    public static RequestHandler findRequestHandler(String requestUri) {
        if (requestUri.contains("?")) {
            int queryParamIndex = requestUri.indexOf("?");
            requestUri = requestUri.substring(0, queryParamIndex);
        }

        for (String uri : requestHandlerMap.keySet()) {
            if (uri.equals(requestUri)) {
                return requestHandlerMap.get(uri);
            }
        }
        return new ResourceHandler();
    }
}
