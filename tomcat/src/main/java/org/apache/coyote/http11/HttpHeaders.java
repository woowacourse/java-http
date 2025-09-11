package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders(final Map<String, List<String>> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public static HttpHeaders from(final InputStream inputStream) throws IOException {
        final Map<String, List<String>> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = HttpParser.readLine(inputStream)).isBlank()) {
            final int colonIndex = headerLine.indexOf(':');
            if (colonIndex == -1) {
                continue;
            }
            final String key = headerLine.substring(0, colonIndex).trim();
            final String value = headerLine.substring(colonIndex + 1).trim();
            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return new HttpHeaders(headers);
    }

    public String getHeader(final String name) {
        final var values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.getFirst();
    }

    public HttpCookies getCookies() {
        final List<String> cookieHeaders = headers.getOrDefault("Cookie", List.of());
        final String combinedCookieHeader = String.join(";", cookieHeaders);
        if (combinedCookieHeader.isBlank()) {
            return new HttpCookies(List.of());
        }
        final List<HttpCookie> cookies = HttpCookie.parse(combinedCookieHeader);
        return new HttpCookies(cookies);
    }

    public int getContentLength() {
        final List<String> contentLength = headers.get("Content-Length");
        if (contentLength == null || contentLength.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(contentLength.getFirst());
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
