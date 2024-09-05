package org.apache.coyote.handler;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Response;

public class RootRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return "/".equals(httpRequest.getRequestURI());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return Http11Response.builder()
                .protocol(httpRequest.getVersionOfProtocol())
                .statusCode(200)
                .statusMessage("OK")
                .appendHeader("Content-Type", "text/html;charset=utf-8 ")
                .body("Hello world!")
                .build();
    }
}
