package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        String path = request.path();
        String filePath = path.startsWith("/") ? path.substring(1) : path;

        return StaticFileResponseBuilder.buildStaticFileResponse(filePath, request.version());
    }
}
