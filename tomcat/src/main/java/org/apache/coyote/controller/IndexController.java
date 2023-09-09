package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        return new MethodNotAllowedController().service(request);
    }

    @Override
    public HttpResponse doGet(final HttpRequest request) {
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .setContent(request.getPath());
    }
}
