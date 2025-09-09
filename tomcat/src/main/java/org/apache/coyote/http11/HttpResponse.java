package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private final String protocolVersion;
    private final HttpStatus status;
    private final MimeType mimeType;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(String protocolVersion, HttpStatus status, MimeType mimeType, String body) {
        this.protocolVersion = protocolVersion;
        this.status = status;
        this.mimeType = mimeType;
        this.headers = new HashMap<>();
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, MimeType contentType, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", status, contentType, body);
        response.initHeaders();
        return response;
    }

    public void initHeaders() {
        if (mimeType != null) {
            headers.put(CONTENT_TYPE_HEADER, mimeType.getType() + ";charset=utf-8");
        }
        if (!body.isEmpty()) {
            headers.put(CONTENT_LENGTH_HEADER, String.valueOf(this.body.getBytes(StandardCharsets.UTF_8).length));
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
