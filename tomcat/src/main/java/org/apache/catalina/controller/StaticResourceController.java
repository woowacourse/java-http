package org.apache.catalina.controller;

import org.apache.catalina.resource.ResourceHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    private final ResourceHandler resources;

    public StaticResourceController(final ResourceHandler resources) {
        this.resources = resources;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        resources.serve(request.getPath(), response);
    }

}
