package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;

import java.net.URI;

public class GetLoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path) && httpRequest.getMethod().isGet();
    }

    @Override
    protected String forward(HttpRequest httpRequest) {
        return "login.html";
    }
}
