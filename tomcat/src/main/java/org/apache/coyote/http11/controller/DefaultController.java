package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.http.StatusCode.OK;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class DefaultController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String responseBody = readFile(request, request.getResource());
        response.setResponseBody(responseBody);
        response.setStatusLine(new StatusLine(request.getProtocolVersion(), OK.getNumber(), "OK"));
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return false;
    }
}
