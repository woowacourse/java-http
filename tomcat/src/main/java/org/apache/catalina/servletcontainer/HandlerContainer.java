package org.apache.catalina.servletcontainer;

import nextstep.jwp.handler.FileHandler;
import org.apache.coyote.http11.Container;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class HandlerContainer implements Container {

    private final Map<String, Handler> handlers = new HashMap<>();

    private static final Handler fileHandler = new FileHandler();

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String requestUri = httpRequest.getEndPoint();
        Handler handler = findHandler(requestUri);
        handler.handle(httpRequest, httpResponse);
    }

    @Override
    public void addHandler(String url, Handler handler) {
        this.handlers.put(url, handler);
    }

    private Handler findHandler(String requestUri) {
        return handlers.getOrDefault(requestUri, fileHandler);
    }
}
