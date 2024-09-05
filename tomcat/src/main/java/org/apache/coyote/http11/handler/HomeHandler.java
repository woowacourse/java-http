package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseBody("Hello world!");
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");

        httpResponse.write();
    }
}
