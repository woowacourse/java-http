package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpStatus status;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void send(OutputStream outputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(status.getCode()).append(" ").append(status.getMessage())
                .append("\r\n");

        for(Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        response.append("\r\n");
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
