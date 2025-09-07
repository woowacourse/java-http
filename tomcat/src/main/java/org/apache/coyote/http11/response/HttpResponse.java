package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(StatusLine statusLine, Map<String, String> headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(Map<String, String> headers, byte[] body) {
        this(new StatusLine(), headers, body);
    }

    public HttpResponse(Map<String, String> headers) {
        this(new StatusLine(), headers, null);
    }

    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();

        sb.append(statusLine.toString()).append("\r\n");

        for (Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        sb.append("\r\n");

        byte[] headerBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        if (body == null) {
            return headerBytes;
        }

        byte[] result = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(body, 0, result, headerBytes.length, body.length);

        return result;
    }
}
