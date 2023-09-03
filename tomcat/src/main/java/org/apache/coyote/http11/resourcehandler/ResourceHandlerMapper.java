package org.apache.coyote.http11.resourcehandler;

import org.apache.coyote.http11.request.HttpRequest;

public class ResourceHandlerMapper {

    private ResourceHandlerMapper() {
    }

    public static ResourceHandler findHandler(final HttpRequest request) {
        if ("/".equals(request.getRequestUri())) {
            return new DefaultResourceHandlers();
        }
        return new StaticResourceHandler();
    }
}
