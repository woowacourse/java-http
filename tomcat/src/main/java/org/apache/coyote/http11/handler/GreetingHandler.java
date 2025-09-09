package org.apache.coyote.http11.handler;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class GreetingHandler implements HttpHandler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return new HttpResponse(HttpStatus.OK, new LinkedHashMap<>(), "Hello world!".getBytes());
    }
}
