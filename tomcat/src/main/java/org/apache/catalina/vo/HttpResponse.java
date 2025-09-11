package org.apache.catalina.vo;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String version;
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(String version, HttpStatus status, Map<String, String> headers, String body) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
        addHeader("Content-Length", body.getBytes().length);
    }

    public static HttpResponse of(HttpStatus status, String body) {
        return new HttpResponse("1.1", status, new HashMap<>(), body);
    }

    public static HttpResponse of(HttpStatus status) {
        return new HttpResponse("1.1", status, new HashMap<>(), "");
    }

    public void setContentType(final String value) {
        addHeader("Content-Type", value + ";charset=utf-8");
    }

    public void addCookie(final Cookie cookie) {
        headers.put("Set-Cookie", cookie.toString());
    }

    public void addHeader(final String name, final Object value) {
        headers.put(name, String.valueOf(value));
    }

    public int getStatusCode() {
        return status.value();
    }

    public String getStatusReason() {
        return status.reason();
    }

    public String getStatusLine() {
        return String.format("HTTP/%s %d %s", version, getStatusCode(), getStatusReason());
    }

    public String getHeaderString() {
        if (headers.isEmpty()) {
            return "\r\n";
        }
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            result.append(String.format("%s: %s", key, headers.get(key)));
            result.append("\r\n");
        }
        return result.toString();
    }

    public String getBody() {
        return body;
    }
}
