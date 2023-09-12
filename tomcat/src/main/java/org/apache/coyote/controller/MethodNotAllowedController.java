package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class MethodNotAllowedController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setContent("/405.html");
    }
}
