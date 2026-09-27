package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record HttpResponse(OutputStream output, String setCookie) {

    public HttpResponse(OutputStream output) {
        this(output, null);
    }

    public void send(String contentType, String body) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + bodyBytes.length + "\r\n"
                + cookieHeader()
                + "\r\n";

        output.write(headers.getBytes(StandardCharsets.UTF_8));
        output.write(bodyBytes);
    }

    public void sendRedirect(String location) throws IOException {
        String response = "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + cookieHeader()
                + "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

    public void sendBadRequest() throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\n"
                + "Content-Length: 0\r\n"
                + cookieHeader()
                + "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

    private String cookieHeader() {
        if (setCookie == null) {
            return "";
        }
        return "Set-Cookie: " + setCookie + "\r\n";
    }
}
