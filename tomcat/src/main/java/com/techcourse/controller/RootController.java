package com.techcourse.controller;

import static org.apache.coyote.http11.response.HttpResponseHeaderNames.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeaderNames.CONTENT_TYPE;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeTypes;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.getPath().equals("/")) {
            response.addHeader(CONTENT_LENGTH.getHeaderName(), Integer.toString("Hello world!".length()));
            response.addHeader(CONTENT_TYPE.getHeaderName(), MimeTypes.getMimeTypes("html"));
            response.setResponseBody("Hello world!");
        }
    }
}
