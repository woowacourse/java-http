package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexHandler extends AbstractRequestHandler {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String responseBody = "Hello world!";

        response.setResponseBody(responseBody);
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        response.write();
    }
}
