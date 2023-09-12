package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class DefaultController extends AbstractController{

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK).setResponseFileName(request.path());
    }
}
