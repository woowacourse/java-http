package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;

public class HttpResourceHandler implements HttpHandler {

    private final HttpResourceLoader httpResourceLoader;

    public HttpResourceHandler(final HttpResourceLoader httpResourceLoader) {
        this.httpResourceLoader = httpResourceLoader;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws Exception {
        String path = request.path();
        return httpResourceLoader.load(path);
    }
}
