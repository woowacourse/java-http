package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpHeaders {

    private static final int MAX_LINE_LENGTH = 8192;

    private final Map<String, List<String>> headers;

    public HttpHeaders(final Map<String, List<String>> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public static HttpHeaders from(final InputStream inputStream) throws IOException {
        final Map<String, List<String>> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = readLine(inputStream)).isBlank()) {
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

    private static String readLine(final InputStream inputStream) throws IOException {
        final var buffer = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            if (buffer.size() >= MAX_LINE_LENGTH) {
                throw new IOException("요청 라인/헤더가 최대 길이 " + MAX_LINE_LENGTH + "를 초과합니다.");
            }
            if (nextByte == '\n') {
                break;
            }
            if (nextByte == '\r') {
                inputStream.read();
                break;
            }
            buffer.write(nextByte);
        }
        return buffer.toString(StandardCharsets.US_ASCII);
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
