package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static final String ROOT_PATH = "/";
    private static final String WELCOME_PAGE = "/index.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        StaticResource.serve(resourcePath(request.getPath()), response);
    }

    private String resourcePath(final String path) {
        if (path.equals(ROOT_PATH)) {
            return WELCOME_PAGE;
        }
        return path;
    }
}
