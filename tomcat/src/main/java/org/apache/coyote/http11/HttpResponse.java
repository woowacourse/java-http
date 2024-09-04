package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final double version;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse(double version, int statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        this.body = "";
    }

    public HttpResponse addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public String toHttpMessage() {
        final var response = new StringBuilder();
        response.append("HTTP/").append(version).append(" ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
        headers.forEach((key, value) -> response.append(key).append(": ").append(value).append("\r\n"));
        response.append("\r\n");
        response.append(body);
        return response.toString();
    }

    public byte[] getAsBytes() {
        return toHttpMessage().getBytes();
    }
}
