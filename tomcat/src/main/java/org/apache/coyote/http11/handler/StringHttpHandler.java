package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class StringHttpHandler implements HttpHandler {

    private final String body;

    public StringHttpHandler(String body) {
        this.body = body;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = HttpResponse.of(HttpStatus.OK, body);
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        return response;
    }
}
