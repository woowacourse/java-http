package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RootController extends AbstractController {

    private static final String ROOT_PATH = "/";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatus(HttpStatus.OK);
        response.setPath(ROOT_PATH);
        response.setBody("Hello world!");
    }
}
