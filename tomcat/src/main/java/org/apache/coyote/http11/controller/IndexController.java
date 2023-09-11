package org.apache.coyote.http11.controller;

import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setContentBody("Hello world!");
        response.setHttpStatus(HttpStatus.OK);
    }
}
