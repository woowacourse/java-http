package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class GreetingController implements HttpRequestHandler {

    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;

    @Override
    public boolean supports(HttpRequest request) {
        return request.methodEquals(SUPPORTING_METHOD) &&
                request.protocolEquals(SUPPORTING_PROTOCOL) &&
                request.isUriHome();
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return HttpResponse.ok("Hello world!", "html");
    }
}
