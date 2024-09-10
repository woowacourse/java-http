package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.HttpHeader;

public class ResourceController extends Controller {

    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    @Override
    public boolean service(HttpRequest request, HttpResponse response) {
        if (request.isTargetBlank()) {
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.setBody(BASIC_RESPONSE_BODY);
            return true;
        }
        if (request.isTargetStatic()) {
            return responseFile(request, response);
        }
        return false;
    }

    private boolean responseFile(HttpRequest request, HttpResponse response) {
        if (request.uriStartsWith("/login") && request.getSessionFromCookie().isPresent()) {
            redirectTo(response, "/index");
        }
        return responseResource(response, request.getTargetPath());
    }
}
