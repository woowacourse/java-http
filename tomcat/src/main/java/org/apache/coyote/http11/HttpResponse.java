package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private StatusLine statusLine;
    private ResponseHeaders headers;
    private byte[] body;

    public HttpResponse(final StatusLine statusLine, final ResponseHeaders headers, final byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse empty() {
        return new HttpResponse(StatusLine.empty(), ResponseHeaders.empty(), "".getBytes());
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(final String statusLine) {
        this.statusLine = StatusLine.from(statusLine);
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public Map<String, String> getHeaders() {
        addDefaultHeaders();
        return new HashMap<>(headers.getHeaders());
    }

    private void addDefaultHeaders() {
        if (headers.get("Content-Type") == null) {
            headers.put("Content-Type", DEFAULT_CONTENT_TYPE);
        }
    }

    public void putHeader(String name, String field) {
        headers.put(name, field);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(final byte[] body) {
        headers.put("Content-Length", String.valueOf(body.length));
        this.body = body;
    }
}
