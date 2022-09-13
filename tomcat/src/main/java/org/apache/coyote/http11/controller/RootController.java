package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RootController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.addStatusLine(HttpStatus.OK.getStatusCodeAndMessage());
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBody("Hello world!");
    }
}
