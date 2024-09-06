package com.techcourse.handler;

import org.apache.coyote.http11.AbstractHandler;
import org.apache.coyote.http11.ForwardResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

public class NotFoundHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest) {
        return new ForwardResult("404.html", HttpStatus.NOT_FOUND);
    }
}
