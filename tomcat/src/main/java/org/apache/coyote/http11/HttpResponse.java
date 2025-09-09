package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private final String protocolVersion;
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(String protocolVersion, HttpStatus status, Map<String, String> headers, String body) {
        this.protocolVersion = protocolVersion;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String toHttpString() {
        StringBuilder response = new StringBuilder();
        response.append(protocolVersion)
                .append(" ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append("\r\n");
        
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        
        response.append("\r\n");
        if (body != null && !body.isEmpty()) {
            response.append(body);
        }
        
        return response.toString();
    }
}
