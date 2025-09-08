package org.apache.catalina.exception;

import org.apache.catalina.resolver.ViewResolver;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    private final ViewResolver viewResolver;

    public ExceptionHandler(final ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public void handle(final Exception e, final Http11Request request, final Http11Response response) {
        String resourcePath = request.parseResourcePath();
        log.error("Exception occurred while processing request for resource: {}", resourcePath, e);
        viewResolver.resolve(response.getState().getResourcePath(), response);
    }
}
