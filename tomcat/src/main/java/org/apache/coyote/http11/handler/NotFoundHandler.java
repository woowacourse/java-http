package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;

public class NotFoundHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    protected String forward(HttpRequest httpRequest) {
        return "404.html";
    }
}
