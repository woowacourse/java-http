package org.apache.catalina.handler;

import java.io.IOException;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resource.ResourceLoader;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticResourceRequestHandler implements RequestHandler {

    private final ResourceLoader resourceLoader;

    public StaticResourceRequestHandler(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean canHandle(Http11Request request) {
        return request.parseResourcePath().contains(".");
    }

    @Override
    public void handle(Http11Request request, Http11Response response) {
        String resourcePath = request.parseResourcePath();
        try {
            byte[] responseBody = resourceLoader.getResponseBody(resourcePath);
            response.setBody(responseBody);
        } catch (IOException e) {
            response.setState(HttpStatus.NOT_FOUND);
            throw new PathNotFoundException(e);
        }
    }
}
