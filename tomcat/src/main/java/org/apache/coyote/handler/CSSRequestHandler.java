package org.apache.coyote.handler;

import org.apache.coyote.HttpRequest;

public class CSSRequestHandler extends ResourceRequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return false;
        }
        return httpRequest.getRequestURI().endsWith("css");
    }

    @Override
    public String getContentType(HttpRequest httpRequest) {
        return httpRequest.getHeaderValue("Accept");
    }
}
