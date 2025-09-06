package org.apache.coyote.http11.message.response;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;

public class HttpResponse {
    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpResponse(StatusLine statusLine, HttpHeaders headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(statusLine).append("\r\n");

        headers.keySet().forEach(key ->
            headers.getHeaders(key).forEach(value -> sb.append(key).append(": ").append(value).append("\r\n"))
        );
        sb.append("\r\n");

        if (body != null) {
            sb.append(new String(body, StandardCharsets.UTF_8));
        }

        return sb.toString();
    }
}
