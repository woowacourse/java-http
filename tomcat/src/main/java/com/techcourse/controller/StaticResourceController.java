package com.techcourse.controller;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String responseBody = new String(request.toHttpResponseBody());

        response.addVersion(request.getVersion());
        response.addHttpStatus(HttpStatus.OK);
        response.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        response.addHeader(HttpHeaders.CONTENT_LENGTH, responseBody.getBytes().length);
        response.addBody(responseBody);
    }
}
