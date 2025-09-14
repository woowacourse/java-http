package org.apache.catalina.exception;

import org.apache.catalina.Resolver;
import org.apache.catalina.handler.FileExtension;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    private final Resolver resolver;

    public ExceptionHandler(final Resolver resolver) {
        this.resolver = resolver;
    }

    public void handle(final Exception e, final Http11Request request, final Http11Response response) {
        final String resourcePath = request.parseResourcePath();
        log.error("Exception occurred while processing request for resource: {}", resourcePath, e);
        response.setContentType(FileExtension.HTML.getMimeType());
        response.setContentLength();
        resolver.resolve(response.getState().getResourcePath(), response);
    }
}
