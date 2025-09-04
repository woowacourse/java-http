package org.apache.catalina.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {

    private String startLine;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse(String startLine, Map<String, String> headers, byte[] body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse() {
        this(null, new LinkedHashMap<>(), null);
    }

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
