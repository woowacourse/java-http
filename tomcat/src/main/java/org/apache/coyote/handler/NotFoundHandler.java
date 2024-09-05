package org.apache.coyote.handler;

import static org.apache.ResourceReader.readFile;

import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public class NotFoundHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        return false;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        return Http11Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .appendHeader("Content-Type", "text/html;charset=utf-8 ")
                .body(readFile("/404.html"))
                .build();
    }
}
