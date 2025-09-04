package org.apache.coyote.handler;

import java.io.IOException;

public class HtmlHandler extends AbstractHandler {

    @Override
    public boolean canHandle(final String requestTarget) {
        return requestTarget.endsWith(".html");
    }

    @Override
    public String handle(final String requestUri) throws IOException {
        final var responseBody = getStaticResponseBody(requestUri);
        if (responseBody == null) {
            return createNotFoundResponse();
        }

        return createOkResponse(responseBody, "text/html;charset=utf-8");
    }
}
