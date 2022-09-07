package org.apache.controller;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

public class HelloController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final String responseBody = "Hello world";
        return new HttpResponse(HttpStatus.OK, ContentType.HTML, responseBody);
    }
}
