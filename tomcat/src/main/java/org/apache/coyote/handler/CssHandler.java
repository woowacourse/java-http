package org.apache.coyote.handler;

import java.io.IOException;

public class CssHandler extends AbstractHandler {

    @Override
    public boolean canHandle(final String requestTarget) {
        return requestTarget.endsWith(".css");
    }

    @Override
    public String handle(final String requestTarget) throws IOException {
        final var responseBody = getStaticResponseBody(requestTarget);
        if (responseBody == null) {
            return "HTTP/1.1 404 NOT FOUND ";
        }

        return createResponse(responseBody, "text/css");
    }
}
