package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.net.URI;

public class IndexHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/".equals(path);
    }

    @Override
    protected String forward(HttpRequest httpRequest) {
        return null;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return new HttpResponse("Hello world!".getBytes(), "text/html;charset=utf-8");
    }
}
