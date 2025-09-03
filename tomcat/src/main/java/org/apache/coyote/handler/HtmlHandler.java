package org.apache.coyote.handler;

import java.io.IOException;

public class HtmlHandler extends AbstractHandler {

    @Override
    public boolean canHandler(final String requestTarget) {
        return requestTarget.endsWith(".html");
    }

    @Override
    public String handle(final String requestTarget) throws IOException {
        final var responseBody = getResponseBody(requestTarget);
        if (responseBody == null) {
            return "HTTP/1.1 404 NOT FOUND ";
        }

        return createResponse(responseBody, "text/html;charset=utf-8");
    }
}
