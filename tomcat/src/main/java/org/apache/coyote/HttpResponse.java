package org.apache.coyote;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Map;

public record HttpResponse(
        String version,
        String statusCode,
        Map<String, String> headers,
        byte[] body
) {

    public HttpResponse {
        Objects.requireNonNull(version);
        Objects.requireNonNull(statusCode);
        body = Objects.requireNonNull(body).clone();
        Map<String, String> responseHeaders = new LinkedHashMap<>();
        headers.forEach((name, value) -> {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            responseHeaders.put(name.equalsIgnoreCase("Content-Length") ? "Content-Length" : name, value);
        });
        responseHeaders.put("Content-Length", Integer.toString(body.length));
        headers = Collections.unmodifiableMap(responseHeaders);
    }

    @Override
    public byte[] body() {
        return body.clone();
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
