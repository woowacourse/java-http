package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final String protocolVersion;
    private final HttpStatus status;
    private final String contentType;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(String protocolVersion, HttpStatus status, String contentType, String body) {
        this.protocolVersion = protocolVersion;
        this.status = status;
        this.contentType = contentType;
        this.headers = new HashMap<>();
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, String contentType, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", status, contentType, body);
        response.initHeaders();
        return response;
    }

    public void initHeaders() {
        if (!contentType.isBlank()) {
            headers.put(CONTENT_TYPE, "text/" + this.contentType + ";charset=utf-8");
        }
        if (!body.isEmpty()) {
            headers.put(CONTENT_LENGTH, String.valueOf(this.body.getBytes().length));
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toHttpResponseString() {
        StringBuilder sb = new StringBuilder();
        buildHeaders(sb);
        appendBody(sb);
        return sb.toString();
    }

    private void buildHeaders(StringBuilder sb) {
        sb.append(this.protocolVersion)
                .append(" ")
                .append(status.getCodeWithMessage())
                .append("\r\n");

        for (String key : headers.keySet()) {
            sb.append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append("\r\n");
        }
        sb.append("\r\n");
    }

    private void appendBody(StringBuilder sb) {
        if (!body.isEmpty()) {
            sb.append(this.body);
        }
    }
}
