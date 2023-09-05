package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class MethodNotAllowedHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .setContent("/405.html");
    }
}
