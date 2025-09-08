package org.apache.coyote.http11.handler;

import java.util.Map;

public class HttpRequestHandlerContainer {

    private final Map<String, HttpRequestHandler> handlers;

    public HttpRequestHandlerContainer() {
        this.handlers = Map.ofEntries(
                getHandlerMapping(new RootPathHandler()),
                getHandlerMapping(new StaticResourceHandler("/index.html")),
                getHandlerMapping(new StaticResourceHandler("/assets/chart-area.js")),
                getHandlerMapping(new StaticResourceHandler("/assets/chart-bar.js")),
                getHandlerMapping(new StaticResourceHandler("/assets/chart-pie.js")),
                getHandlerMapping(new StaticResourceHandler("/css/styles.css")),
                getHandlerMapping(new StaticResourceHandler("/js/scripts.js")),
                getHandlerMapping(new LoginHandler())
        );
    }

    private Map.Entry<String, HttpRequestHandler> getHandlerMapping(HttpRequestHandler httpRequestHandler) {
        return Map.entry(httpRequestHandler.getSupportedUrl(), httpRequestHandler);
    }

    public HttpRequestHandler getHandler(String url) {
        return handlers.get(url);
    }
}
