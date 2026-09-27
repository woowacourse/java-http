package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String path = request.getPath();
        response.ok(StaticResource.contentType(path), StaticResource.read(path));
    }
}
