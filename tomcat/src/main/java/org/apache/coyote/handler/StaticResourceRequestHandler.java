package org.apache.coyote.handler;

import static org.apache.ResourceReader.canRead;
import static org.apache.ResourceReader.readFile;

import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return canRead(httpRequest.getRequestURI()) && Http11Method.GET.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        return Http11Response.builder()
                .status(HttpStatus.OK)
                .appendHeader("Content-Type", getContentType(httpRequest))
                .body(readFile(httpRequest.getRequestURI()))
                .build();
    }

    private String getContentType(HttpRequest httpRequest) {
        if (httpRequest.getHeader("Accept") == null) {
            return "";
        }
        return httpRequest.getHeader("Accept").split(",")[0];
    }
}
