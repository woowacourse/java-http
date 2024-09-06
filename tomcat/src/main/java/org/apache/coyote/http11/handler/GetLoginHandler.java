package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

import java.net.URI;

public class GetLoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path) && httpRequest.getMethod().isGet();
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest) {
        return new ForwardResult("login.html", HttpStatus.OK);
    }
}
