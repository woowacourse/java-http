package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

public class DefaultHandler implements HttpHandler {

    private final String resourcePath;

    public DefaultHandler(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public String getPath() {
        return resourcePath;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return StaticResourceFinder.render(resourcePath);
    }
}
