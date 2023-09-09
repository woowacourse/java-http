package org.apache.coyote.controller;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) {
        String body = "Hello world!";

        response.setHttpStatus(HttpStatus.OK);
        response.addHeaders(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.addHeaders(CONTENT_TYPE, request.getResourceTypes());
        response.setResponseBody(body);
    }
}
