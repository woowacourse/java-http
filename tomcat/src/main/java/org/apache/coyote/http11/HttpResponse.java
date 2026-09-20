package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {

    private static final String CRLF = "\r\n";

    private final String status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final byte[] body;

    public HttpResponse(String status, String contentType, byte[] body) {
        this.status = status;
        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", body.length + " ");
        this.body = body.clone();
    }

    public HttpResponse addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public byte[] toByteArray() {
        var responseHead = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(status)
                .append(" ")
                .append(CRLF);

        headers.forEach((name, value) -> responseHead
                .append(name)
                .append(": ")
                .append(value)
                .append(CRLF));
        responseHead.append(CRLF);

        var response = new ByteArrayOutputStream();
        response.writeBytes(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        response.writeBytes(body);
        return response.toByteArray();
    }
}
