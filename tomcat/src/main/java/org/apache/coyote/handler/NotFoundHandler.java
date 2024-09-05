package org.apache.coyote.handler;

import static org.apache.ResourceReader.readFile;

import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Response.Http11ResponseBuilder;

public class NotFoundHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        return false;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        Http11ResponseBuilder responseBuilder = Http11Response.builder()
                .protocol("HTTP/1.1");
        return responseBuilder
                .statusCode(404)
                .statusMessage("Not Found")
                .appendHeader("Content-Type", "text/html;charset=utf-8 ")
                .body(readFile("/404.html"))
                .build();
    }
}
