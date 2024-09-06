package org.apache.coyote.handler;

import static org.apache.ResourceReader.readFile;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.Http11Response;

public class NotFoundHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return Http11Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .appendHeader("Content-Type", "text/html;charset=utf-8 ")
                .body(readFile("/404.html"))
                .build();
    }
}
