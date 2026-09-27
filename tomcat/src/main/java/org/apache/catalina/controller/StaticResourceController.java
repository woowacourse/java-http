package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.resource.StaticResourceRenderer;
import org.apache.coyote.http11.HttpResponse;

public final class StaticResourceController extends MethodDispatchingController {
    private final StaticResourceRenderer staticResourceRenderer;

    public StaticResourceController(final StaticResourceRenderer staticResourceRenderer) {
        this.staticResourceRenderer = staticResourceRenderer;
    }

    @Override
    protected void doGet(final Request request, final HttpResponse response) throws IOException {
        staticResourceRenderer.render(request.path(), response);
    }
}
