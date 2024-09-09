package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String responseBody = new String(request.toHttpResponseBody());

        response.addVersion(request.getVersion());
        response.addStatusCode(200);
        response.addStatusMessage("OK");
        response.addHeader("Content-Type", request.getContentType());
        response.addHeader("Content-Length", responseBody.getBytes().length);
        response.addBody(responseBody);
    }
}
