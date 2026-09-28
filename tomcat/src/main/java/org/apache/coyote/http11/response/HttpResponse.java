package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class HttpResponse {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final HttpStatus status;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(HttpStatus status, Map<String, String> headers, String body) {
        this.status = Objects.requireNonNull(status);
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = Objects.requireNonNull(body);
    }

    public static HttpResponse ok(String body, String resourceType) {
        return create(HttpStatus.OK, defaultHeaders(resourceType), body);
    }

    public static HttpResponse redirect(String location, String body, String resourceType) {
        Map<String, String> headers = defaultHeaders(resourceType);
        headers.put(LOCATION, location);
        return create(HttpStatus.FOUND, headers, body);
    }

    public static HttpResponse redirectWithCookie(String location, String body,
        String resourceType, String sessionId) {
        Map<String, String> headers = defaultHeaders(resourceType);
        headers.put(LOCATION, location);
        headers.put(SET_COOKIE, "JSESSIONID=" + sessionId + ";");
        return create(HttpStatus.FOUND, headers, body);
    }

    private static HttpResponse create(HttpStatus status, Map<String, String> headers, String body) {
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength(body)));
        return new HttpResponse(status, headers, body);
    }

    private static Map<String, String> defaultHeaders(String resourceType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, contentType(resourceType));
        return headers;
    }

    private static String contentType(String resourceType) {
        return "text/" + resourceType + ";charset=utf-8";
    }

    private static int contentLength(String body) {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    public HttpStatus status() {
        return status;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Optional<String> header(String name) {
        return headers.entrySet().stream()
            .filter(entry -> entry.getKey().equalsIgnoreCase(name))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    public String body() {
        return body;
    }

    public byte[] toBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        List<String> lines = serializeHeaders();
        lines.add("");
        lines.add(body);
        return String.join("\r\n", lines);
    }

    private List<String> serializeHeaders() {
        List<String> lines = new ArrayList<>();
        lines.add(status.line());
        headers.forEach((name, value) -> lines.add(name + ": " + value));
        return lines;
    }
}
