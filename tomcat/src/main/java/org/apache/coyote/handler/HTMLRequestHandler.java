package org.apache.coyote.handler;

import org.apache.coyote.HttpRequest;

public class HTMLRequestHandler extends ResourceRequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return false;
        }
        return httpRequest.getRequestURI().endsWith("html");
    }

    @Override
    public String getContentType(HttpRequest httpRequest) {
        return "text/html;charset=utf-8 ";
    }
}
