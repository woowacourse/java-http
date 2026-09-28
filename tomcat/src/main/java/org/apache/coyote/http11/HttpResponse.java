package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class HttpResponse {

    private final OutputStream outputStream;

    private String sessionCookie;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addCookie(final String cookie) {
        sessionCookie = cookie;
    }

    public void send(final String contentType, final byte[] body) throws IOException {
        final var headers = "HTTP/1.1 200 OK \r\n"
                + createSetCookieHeader()
                + String.join("\r\n",
                        "Content-Type: " + contentType + " ",
                        "Content-Length: " + body.length + " ",
                        "",
                        ""
                );

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendRedirect(final String location) throws IOException {
        final var response = "HTTP/1.1 302 Found\r\n"
                + createSetCookieHeader()
                + String.join("\r\n",
                        "Location: " + location,
                        "Content-Length: 0",
                        "",
                        ""
                );

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String createSetCookieHeader() {
        if (sessionCookie == null) {
            return "";
        }

        return "Set-Cookie: " + sessionCookie + "\r\n";
    }
}
