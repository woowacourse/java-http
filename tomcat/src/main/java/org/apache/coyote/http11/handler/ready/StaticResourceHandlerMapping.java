package org.apache.coyote.http11.handler.ready;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.request.dto.HttpRequest;

public class StaticResourceHandlerMapping implements HandlerMapping {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Controller getHandler(HttpRequest request) {
        if (hasStaticResource(request.path())) {
            return new StaticResourceController("static", "index.html");
        }
        return null;
    }

    private boolean hasStaticResource(String path) {
        String resourcePath = path.replaceFirst("^/static/?", "");
        if (resourcePath.isEmpty()) resourcePath = "index.html";
        String fullPath = "static/" + resourcePath;
        return getClass().getClassLoader().getResource(fullPath) != null;
    }
}
