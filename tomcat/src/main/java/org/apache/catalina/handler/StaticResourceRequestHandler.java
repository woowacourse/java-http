package org.apache.catalina.handler;

import java.io.IOException;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resource.ResourceLoader;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticResourceRequestHandler implements RequestHandler {

    private final ResourceLoader resourceLoader;

    public StaticResourceRequestHandler(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean canHandle(final Http11Request request) {
        return request.parseResourcePath().contains(".");
    }

    @Override
    public void handle(final Http11Request request, final Http11Response response) {
        final String resourcePath = request.parseResourcePath();
        try {
            final byte[] responseBody = resourceLoader.getResponseBody(resourcePath);
            response.setBody(responseBody);
        } catch (IOException e) {
            response.setState(HttpStatus.NOT_FOUND);
            throw new PathNotFoundException(e);
        }
    }
}
