package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class MethodNotAllowedHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpResponse initialResponse = HttpResponse.init(request.getHttpVersion());
        final HttpResponse afterSetHttpStatus = initialResponse.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        final HttpResponse afterSetContent = afterSetHttpStatus.setContent("/405.html");
        return afterSetContent;
    }
}
