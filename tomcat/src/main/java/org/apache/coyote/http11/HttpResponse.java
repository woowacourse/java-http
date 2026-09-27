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
        writeResponse("200 OK", "Content-Type: " + contentType + "\r\n", bodyBytes);
    }

    public void sendRedirect(String location) throws IOException {
        writeResponse("302 Found", "Location: " + location + "\r\n", new byte[0]);
    }

    public void sendBadRequest() throws IOException {
        writeResponse("400 Bad Request", "", new byte[0]);
    }

    public void sendNotFound() throws IOException {
        writeResponse("404 Not Found", "", new byte[0]);
    }

    private void writeResponse(String status, String extraHeaders, byte[] body) throws IOException {
        String headers = "HTTP/1.1 " + status + "\r\n"
                + extraHeaders
                + "Content-Length: " + body.length + "\r\n"
                + cookieHeader()
                + "\r\n";
        output.write(headers.getBytes(StandardCharsets.UTF_8));
        output.write(body);
    }

    private String cookieHeader() {
        if (setCookie == null) {
            return "";
        }
        return "Set-Cookie: " + setCookie + "\r\n";
    }
}
