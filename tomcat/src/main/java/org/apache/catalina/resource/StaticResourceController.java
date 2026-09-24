package org.apache.catalina.resource;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class StaticResourceController extends AbstractController {

    private final StaticResourceService resources = new StaticResourceService();

    public StaticResourceController() {
        super(HttpMethod.GET);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        resources.serve(request.getRequestUri().getPath(), response);
    }
}
