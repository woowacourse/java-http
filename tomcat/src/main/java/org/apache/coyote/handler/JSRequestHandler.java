package org.apache.coyote.handler;

import org.apache.coyote.HttpRequest;

public class JSRequestHandler extends ResourceRequestHandler {
    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return httpRequest.getRequestURI().endsWith("js");
    }

    @Override
    public String getContentType(HttpRequest httpRequest) {
        return httpRequest.getHeaderValue("Accept");
    }
}
