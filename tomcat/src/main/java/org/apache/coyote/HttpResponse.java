package org.apache.coyote;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Map;

public final class HttpResponse {

    private String version;
    private String statusCode;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        this("HTTP/1.1", "200 OK", Map.of("Content-Type", "text/html;charset=utf-8"), new byte[0]);
    }

    public HttpResponse(String version, String statusCode, Map<String, String> headers, byte[] body) {
        this.version = Objects.requireNonNull(version);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.body = Objects.requireNonNull(body).clone();
        Map<String, String> responseHeaders = new LinkedHashMap<>();
        headers.forEach((name, value) -> {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            responseHeaders.put(name.equalsIgnoreCase("Content-Length") ? "Content-Length" : name, value);
        });
        responseHeaders.put("Content-Length", Integer.toString(body.length));
        this.headers = Collections.unmodifiableMap(responseHeaders);
    }

    public void copyFrom(HttpResponse response) {
        version = response.version;
        statusCode = response.statusCode;
        headers = response.headers;
        body = response.body.clone();
    }

    public void sendRedirect(String location) {
        copyFrom(redirect(location));
    }

    public byte[] toBytes() {
        StringBuilder head = new StringBuilder(version).append(" ").append(statusCode).append("\r\n");
        headers.forEach((name, value) ->
                head.append(name).append(": ").append(value).append("\r\n"));
        head.append("\r\n");

        byte[] headBytes = head.toString().getBytes(StandardCharsets.UTF_8);
        byte[] responseBytes = Arrays.copyOf(headBytes, headBytes.length + body.length);
        System.arraycopy(body, 0, responseBytes, headBytes.length, body.length);
        return responseBytes;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return create("200 OK", contentType, body, Map.of());
    }

    public static HttpResponse notFound(byte[] body) {
        return create("404 Not Found", "text/html;charset=utf-8", body, Map.of());
    }

    public static HttpResponse badRequest(byte[] body) {
        return create("400 Bad Request", "text/html;charset=utf-8", body, Map.of());
    }

    public static HttpResponse redirect(String location) {
        return create("302 Found", "text/html;charset=utf-8", new byte[0],
                Map.of("Location", location));
    }

    private static HttpResponse create(String statusCode, String contentType, byte[] body,
                                       Map<String, String> additionalHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", Integer.toString(body.length));
        headers.putAll(additionalHeaders);
        return new HttpResponse("HTTP/1.1", statusCode, headers, body);
    }

    public HttpResponse withCookie(String name, String value) {
        Map<String, String> newHeaders = new LinkedHashMap<>(headers);
        newHeaders.put("Set-Cookie", name + "=" + value);

        return new HttpResponse(version, statusCode, newHeaders, body);
    }
}
