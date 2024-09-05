package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeHandler extends AbstractRequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        response.setResponseBody("Hello world!");
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");

        response.write();
    }
}
