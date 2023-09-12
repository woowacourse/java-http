package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class UnAuthorizedController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatus(HttpStatus.UNAUTHORIZED);
        response.setContent("/401.html");
    }
}
