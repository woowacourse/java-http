package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class UnAuthorizedHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpResponse initialResponse = HttpResponse.init(request.getHttpVersion());
        final HttpResponse afterSetHttpStatus = initialResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        final HttpResponse afterSetContent = afterSetHttpStatus.setContent("/401.html");
        final HttpResponse afterSetCookie = afterSetContent.setCookieHeader(request.getCookieHeader());
        return afterSetCookie;
    }
}
