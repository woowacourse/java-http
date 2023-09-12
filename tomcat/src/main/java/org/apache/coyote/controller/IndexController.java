package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        new MethodNotAllowedController().service(request, response);
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatus(HttpStatus.OK);
        response.setContent(request.getPath());
    }
}
