package org.apache.coyote.http11.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class ResourceHandlerMapper {

    private static final List<ResourceHandler> handlers = new ArrayList<>();
    private static final ResourceHandler unsupportedResourceHandler = new UnsupportedResourceHandler();

    static {
        handlers.add(new DefaultResourceHandlers());
        handlers.add(new StaticResourceHandler());
    }

    private ResourceHandlerMapper() {
    }

    public static ResourceHandler findHandler(final HttpRequest request) {
        return handlers.stream()
                .filter(each -> each.supports(request))
                .findAny()
                .orElse(unsupportedResourceHandler);
    }
}
