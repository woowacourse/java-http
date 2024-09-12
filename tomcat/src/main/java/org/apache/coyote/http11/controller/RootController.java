package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RootController extends AbstractController {

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody("Hello world!");
    }
}
