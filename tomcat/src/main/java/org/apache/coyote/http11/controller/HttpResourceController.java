package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;

public class HttpResourceController extends AbstractController {

    private final HttpResourceLoader httpResourceLoader;

    public HttpResourceController(final HttpResourceLoader httpResourceLoader) {
        this.httpResourceLoader = httpResourceLoader;
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String path = request.getPath();

        HttpResponse actualResponse = httpResourceLoader.load(path);
        response.setHttpResponse(actualResponse);
    }
}
