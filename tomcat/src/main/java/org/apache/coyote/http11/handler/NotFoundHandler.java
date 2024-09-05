package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;

public class NotFoundHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest) {
        return new ForwardResult(false, "404.html");
    }
}
