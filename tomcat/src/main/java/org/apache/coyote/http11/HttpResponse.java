package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        this.status = HttpStatus.OK;
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(byte[] body, String contentType) {
        this.body = body;
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(body.length));
    }

    public void sendRedirect(String location) {
        this.status = HttpStatus.FOUND;
        this.body = new byte[0];
        headers.remove("Content-Type");
        headers.put("Location", location);
        headers.put("Content-Length", "0");
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        headers.put("Content-Length", String.valueOf(body.length));

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }
        response.append("\r\n");

        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

}
