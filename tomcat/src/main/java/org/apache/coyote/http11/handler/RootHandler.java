package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpStatus.*;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootHandler implements HttpRequestHandler{

    @Override
    public void handleGet(HttpRequest request, HttpResponse response) {
        response.setStatus(OK);
        response.setBody("Hello World");
        response.setContentType("text/plain;charset=utf-8");
    }
}
