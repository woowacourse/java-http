package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.ok().contentType("text/html").body("Hello world!");
    }
}
