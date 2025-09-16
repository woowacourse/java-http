package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final OutputStream outputStream;
    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
        headers.put("Content-Type", "text/html;charset=utf-8");
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public void send() throws IOException {
        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        addHeader("Content-Length", String.valueOf(bodyBytes.length));

        final String responseLine = "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + "\r\n";
        outputStream.write(responseLine.getBytes(StandardCharsets.UTF_8));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            final String headerLine = header.getKey() + ": " + header.getValue() + "\r\n";
            outputStream.write(headerLine.getBytes(StandardCharsets.UTF_8));
        }

        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write(bodyBytes);
        outputStream.flush();
    }

    public void sendRedirect(final String location) throws IOException {
        setStatus(HttpStatus.FOUND);
        addHeader("Location", location);
        send();
    }
}