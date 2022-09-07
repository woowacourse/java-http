package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RootController extends AbstractController{

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBody("Hello world!");
    }
}
