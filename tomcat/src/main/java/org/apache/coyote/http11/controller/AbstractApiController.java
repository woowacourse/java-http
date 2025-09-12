package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

public abstract class AbstractApiController extends AbstractController {

    private final String requestPath;

    public AbstractApiController(final String requestPath) {
        this.requestPath = requestPath;
    }

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathEqualsTo(requestPath);
    }
}
