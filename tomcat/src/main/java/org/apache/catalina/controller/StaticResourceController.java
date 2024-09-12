package org.apache.catalina.controller;

import org.apache.catalina.handler.ViewResolver;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticResourceController implements Controller {

    private final ViewResolver viewResolver = new ViewResolver();

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        try {
            final String path = request.getRequestURI();
            final String body = viewResolver.resolve(path);
            response.setStatusCode(HttpStatusCode.OK);
            response.setContent(path, body);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
        }
    }
}
