package org.apache.coyote.handler;

import java.io.IOException;

public class CssHandler extends AbstractHandler {

    @Override
    public boolean canHandle(final String requestTarget) {
        return requestTarget.endsWith(".css");
    }

    @Override
    public String handle(final String requestUri) throws IOException {
        final var responseBody = getStaticResponseBody(requestUri);
        if (responseBody == null) {
            return createNotFoundResponse();
        }

        return createOkResponse(responseBody, "text/css");
    }
}
