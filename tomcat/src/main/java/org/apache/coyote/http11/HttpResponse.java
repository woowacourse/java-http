package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void write(OutputStream outputStream) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        setHeader("Content-Length", String.valueOf(bodyBytes.length));

        writeHeaders(outputStream);
        outputStream.write(bodyBytes);
        outputStream.flush();
    }

    private void writeHeaders(OutputStream outputStream) throws IOException {
        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 ").append(status.getHttpStatus()).append("\r\n");

        for (String name : headers.keySet()) {
            responseHeaders.append(name).append(": ").append(headers.get(name)).append("\r\n");
        }
        responseHeaders.append("\r\n");

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
    }
}
