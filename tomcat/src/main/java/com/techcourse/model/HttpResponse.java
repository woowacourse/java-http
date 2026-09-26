package com.techcourse.model;

import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final String protocolVersion = "HTTP/1.1";
    private final String statusCode;

    private final String statusMessage;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    public static HttpResponse of(String statusCode, String statusMessage, Map<String, List<String>> headers, byte[] body) {
        return new HttpResponse(statusCode, statusMessage, headers, body);
    }

    public static HttpResponse redirect(String statusCode, String statusMessage, String url) {
        return new HttpResponse(
                statusCode,
                statusMessage,
                Map.of("Location", List.of(url)),
                new byte[0]
        );
    }

    public static HttpResponse redirectWithSetCookie(String statusCode, String statusMessage, List<String> httpCookie, String url) {
        return new HttpResponse(
                statusCode,
                statusMessage,
                Map.of("Location", List.of(url),
                        "Set-Cookie", httpCookie),
                new byte[0]
        );
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    private HttpResponse(String statusCode, String statusMessage,
                         Map<String, List<String>> headers,
                         byte[] body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }
}
