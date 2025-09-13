package org.apache.coyote.http11;

import org.apache.catalina.controller.StaticResourceHandler;

public class StaticResourceController extends AbstractController {

    private final StaticResourceHandler staticHandler = new StaticResourceHandler();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        staticHandler.serve(request, response);
    }
}
