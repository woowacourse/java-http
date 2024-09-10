package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

public class HomeHandler implements HttpHandler {

    private static final String HTML_PATH = "index.html";

    private final String path;

    public HomeHandler(final String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return createResponse();
    }

    private HttpResponse createResponse() {
        return StaticResourceFinder.render(HTML_PATH);
    }
}
