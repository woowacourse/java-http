package org.apache.coyote.handler;

import org.apache.ResourceReader;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.Http11Response;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return ResourceReader.canRead(httpRequest.getRequestURI()) && HttpMethod.GET.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return Http11Response.builder()
                .status(HttpStatus.OK)
                .appendHeader("Content-Type", getContentType(httpRequest))
                .body(ResourceReader.readFile(httpRequest.getRequestURI()))
                .build();
    }

    private String getContentType(HttpRequest httpRequest) {
        if (httpRequest.getHeader("Accept") == null) {
            int index = httpRequest.getRequestURI().indexOf(".");
            return "text/" + httpRequest.getRequestURI().substring(index + 1) + ";charset=utf-8 ";
        }
        return httpRequest.getHeader("Accept").split(",")[0];
    }
}
