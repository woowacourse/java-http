package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

public class StaticResourceHandler implements HttpHandler {

    private final String resourcePath;

    public StaticResourceHandler(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public String getUriPath() {
        return resourcePath;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return StaticResourceFinder.render(resourcePath);
    }
}
