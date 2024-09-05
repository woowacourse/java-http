package org.apache.coyote.handler;

import static org.apache.coyote.http11.HttpStatus.OK;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.response.Http11Response;

public class RootRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return "/".equals(httpRequest.getRequestURI());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return Http11Response.builder()
                .status(OK)
                .appendHeader("Content-Type", "text/html;charset=utf-8 ")
                .body("Hello world!")
                .build();
    }
}
