package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class MethodNotAllowedController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .setContent("/405.html");
    }
}
