package org.apache.coyote.http11.controller.impl;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.controller.AbstractController;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        response.setHttpStatus(HttpStatus.OK);
        response.write("Hello world!");
    }
}
