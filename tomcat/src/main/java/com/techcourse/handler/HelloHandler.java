package com.techcourse.handler;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.AbstractHandler;
import org.apache.coyote.http11.ForwardResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

import java.net.URI;

public class HelloHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.uri();
        String path = uri.getPath();

        return "/".equals(path);
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest, Manager sessionManager) {
        return new ForwardResult("hello.html", HttpStatus.OK);
    }
}
