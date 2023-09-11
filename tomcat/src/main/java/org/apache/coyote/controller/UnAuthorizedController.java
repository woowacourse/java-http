package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class UnAuthorizedController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.UNAUTHORIZED)
                .setContent("/401.html");
    }
}
