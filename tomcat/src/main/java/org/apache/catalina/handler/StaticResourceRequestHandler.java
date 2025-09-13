package org.apache.catalina.handler;

import java.io.IOException;
import org.apache.catalina.RequestHandler;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.resources.ResourceManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class StaticResourceRequestHandler implements RequestHandler {

    private final ResourceManager resourceLoader;

    public StaticResourceRequestHandler(final ResourceManager resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean canHandle(final Http11Request request) {
        return ExtensionHandler.hasFileExtension(request.parseResourcePath());
    }

    @Override
    public void handle(final Http11Request request, final Http11Response response) {
        final String resourcePath = request.parseResourcePath();
        try {
            final byte[] responseBody = resourceLoader.getResponseBody(resourcePath);
            response.setBody(responseBody);
        } catch (IOException e) {
            throw new PathNotFoundException(response);
        }
    }
}
