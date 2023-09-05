package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class HttpRedirectResponse extends HttpResponse {

    private final String redirectUri;

    public HttpRedirectResponse(
            final OutputStream outputStream,
            final String redirectUri
    ) {
        super(outputStream, "302", null, null, null);
        this.redirectUri = redirectUri;
    }

    @Override
    public void flush() throws IOException {
        final StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append("302")
                .append(" ")
                .append(System.lineSeparator());
        for (Cookie cookie : cookies) {
            response.append("Set-Cookie: ")
                    .append(cookie.getKey())
                    .append("=")
                    .append(cookie.getValue())
                    .append(" ")
                    .append(System.lineSeparator());
        }
        response.append("Location: ")
                .append(this.redirectUri)
                .append(System.lineSeparator());

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }
}
