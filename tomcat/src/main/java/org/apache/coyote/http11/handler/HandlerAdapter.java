package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;

public class HandlerAdapter {

    public RequestHandler find(final HttpRequest request) {
        String requestUri = request.getStartLine().getHttpRequestUri().getUri();
        if (requestUri.contains("/login")) {
            return new LoginHandler();
        }

        if (requestUri.equals("/")) {
            return new DefaultHandler();
        }
        return new ResourceHandler();
    }
}
