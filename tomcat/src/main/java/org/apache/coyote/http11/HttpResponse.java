package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final float version;
    private int statusCode;
    private String statusMessage;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.version = 1.1F;
        this.statusCode = 200;
        this.statusMessage = "OK";
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    public String getResponse() {
        return String.join("\r\n",
                parseResponseLine(),
                parseHeaders(),
                body);
    }

    private String parseResponseLine() {
        return String.format("HTTP/%.1f %d %s ", version, statusCode, statusMessage);
    }

    private String parseHeaders() {
        return headers.entrySet().stream()
                .map(header -> String.format("%s: %s ", header.getKey(), header.getValue()))
                .collect(Collectors.joining("\r\n"));
    }

    public void redirect(String location) {
        this.statusCode = 302;
        this.statusMessage = "FOUND";
        this.headers.put("Location", location);
    }

    public void statusUnauthorized() {
        this.statusCode = 401;
        this.statusMessage = "UNAUTHORIZED";
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
