package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;

import java.net.URI;

public class HelloHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/".equals(path);
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest) {
        return new ForwardResult(false, "hello.html");
    }
}
