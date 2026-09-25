package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public record HttpResponse(
        HttpStatus status,
        Map<String, String> headers,
        String body
) {
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", DEFAULT_CONTENT_TYPE,
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8"
    );

    public HttpResponse {
        headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    }

    public static HttpResponse empty(final HttpStatus status) {
        return new HttpResponse(status, Map.of(), "");
    }

    public static HttpResponse redirect(final String location, final String newSessionId) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        if (newSessionId != null) {
            headers.put("Set-Cookie", "JSESSIONID=" + newSessionId);
        }
        return new HttpResponse(HttpStatus.FOUND, headers, "");
    }

    public static HttpResponse resource(final HttpStatus status, final String resourcePath, final String body) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", getContentType(resourcePath));
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return new HttpResponse(status, headers, body);
    }

    private static String getContentType(final String resourcePath) {
        final int lastSlashIndex = resourcePath.lastIndexOf("/");
        final int lastDotIndex = resourcePath.lastIndexOf(".");

        if (lastDotIndex <= lastSlashIndex) {
            return DEFAULT_CONTENT_TYPE;
        }
        final String extension = resourcePath.substring(lastDotIndex).toLowerCase(Locale.ROOT);
        return CONTENT_TYPES.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }

    public byte[] toBytes() {
        StringBuilder response = new StringBuilder("HTTP/1.1 ")
                .append(status.getStatusLine())
                .append("\r\n");

        headers.forEach((name, value) -> response
                .append(name)
                .append(": ")
                .append(value)
                .append("\r\n"));

        response.append("\r\n").append(body);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }
}
