package org.apache.coyote.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractHandler {

    public abstract boolean canHandle(final String requestTarget);

    public abstract String handle(final String requestTarget) throws IOException;

    protected String getStaticResponseBody(final String requestTarget) throws IOException {
        try (final InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + requestTarget)) {
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected static String createOkResponse(
            final String responseBody,
            final String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    protected static String createNotFoundResponse() {
        return "HTTP/1.1 404 NOT FOUND ";
    }
}
