package org.apache.coyote.http11.handler.statics;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.handler.HandlerMapping;
import org.apache.coyote.http11.http.request.dto.HttpRequest;

public class StaticResourceHandlerMapping implements HandlerMapping {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Controller getHandler(HttpRequest request) {
        if (!request.hasGet()) {
            return null;
        }
        if (hasStaticResource(request.path())) {
            return new StaticResourceController("static", "index.html");
        }
        return null;
    }

    private boolean hasStaticResource(String path) {
        String resourcePath = path.replaceFirst("^/static/?", "");
        String normalizedResourcePath = normalizePath(resourcePath);
        String fullPath = "static/" + normalizedResourcePath;
        return getClass().getClassLoader().getResource(fullPath) != null;
    }

    private String normalizePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.isEmpty()) {
            path = "index.html";
        }
        return path;
    }
}
