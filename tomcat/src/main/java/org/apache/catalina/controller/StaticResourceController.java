package org.apache.catalina.controller;

import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        try {
            final String path = request.getRequestURI();
            response.setStatusCode(HttpStatusCode.OK);
            response.setContent(path);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
        }
    }
}
