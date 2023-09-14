package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class NotFoundController extends AbstractController {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        response.setPath(NOT_FOUND_PAGE);
    }
}
