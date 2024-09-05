package org.apache.coyote.handler;

import static org.apache.ResourceReader.canRead;
import static org.apache.ResourceReader.readFile;

import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Response.Http11ResponseBuilder;

public class ResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return canRead(httpRequest.getPath()) && Http11Method.GET.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        Http11ResponseBuilder responseBuilder = Http11Response.builder()
                .protocol(httpRequest.getVersionOfProtocol());
        return responseBuilder
                .statusCode(200)
                .statusMessage("OK")
                .appendHeader("Content-Type", getContentType(httpRequest))
                .body(readFile(httpRequest.getPath()))
                .build();
    }

    private String getContentType(HttpRequest httpRequest) {
        if (httpRequest.getHeaderValue("Accept") == null) {
            return "";
        }
        return httpRequest.getHeaderValue("Accept").split(",")[0];
    }
}
