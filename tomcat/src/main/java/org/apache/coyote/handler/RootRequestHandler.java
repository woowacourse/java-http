package org.apache.coyote.handler;

import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.RequestHandler;

public class RootRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return "/".equals(httpRequest.getRequestURI());
    }

    @Override
    public String getContentType(HttpRequest httpRequest) {
        return "text/html;charset=utf-8 ";
    }

    @Override
    public String getResponseBody(HttpRequest httpRequest) throws IOException {
        return "Hello world!";
    }
}
