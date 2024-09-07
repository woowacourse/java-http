package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final float DEFAULT_HTTP_VERSION = 1.1F;
    private static final int DEFAULT_HTTP_STATUS_CODE = 200;
    private static final String DEFAULT_HTTP_STATUS_MESSAGE = "OK";
    private static final String DEFAULT_HTTP_BODY = "";

    private float version;
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.version = DEFAULT_HTTP_VERSION;
        this.statusCode = DEFAULT_HTTP_STATUS_CODE;
        this.statusMessage = DEFAULT_HTTP_STATUS_MESSAGE;
        this.headers = new LinkedHashMap<>();
        this.body = DEFAULT_HTTP_BODY;
    }

    public String parseResponse() {
        return String.join("\r\n",
                parseResponseLine(),
                parseHeaders(),
                "",
                body).trim();
    }

    private String parseResponseLine() {
        return String.format("HTTP/%.1f %d %s ", version, statusCode, statusMessage);
    }

    private String parseHeaders() {
        return headers.entrySet().stream()
                .map(header -> String.format("%s: %s ", header.getKey(), header.getValue()))
                .collect(Collectors.joining("\r\n"));
    }

    public void setStatusUnauthorized() {
        this.statusCode = 401;
        this.statusMessage = "UNAUTHORIZED";
    }

    public void sendRedirection(String location) {
        this.statusCode = 302;
        this.statusMessage = "FOUND";
        this.headers.put("Location", location);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
