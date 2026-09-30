package com.techcourse;

import com.techcourse.controller.RequestMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestHandler;

public class HttpRequestHandler implements RequestHandler {

    private final RequestMapping requestMapping;

    public HttpRequestHandler(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {
        var controller = requestMapping.getController(request.getRequestTarget());
        return controller.service(request);
    }
}
