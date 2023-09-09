package org.apache.coyote.http11.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class ResourceHandlerMapper {

    private static final List<Controller> handlers = new ArrayList<>();
    private static final Controller unsupportedResourceHandler = new UnsupportedResourceHandler();

    static {
        handlers.add(new DefaultResourceHandler());
        handlers.add(new StaticResourceHandler());
    }

    private ResourceHandlerMapper() {
    }

    public static Controller findHandler(final HttpRequest request) {
        return handlers.stream()
                .filter(each -> each.supports(request))
                .findAny()
                .orElse(unsupportedResourceHandler);
    }
}
