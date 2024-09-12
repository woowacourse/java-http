package org.apache.coyote.controller;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class StaticResourceController extends AbstractController {

    private static final String DEFAULT_PATH = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = DEFAULT_PATH + request.getPath();

        response.setStatusLine(HttpStatus.OK);
        response.setStaticBody(path);
    }
}
