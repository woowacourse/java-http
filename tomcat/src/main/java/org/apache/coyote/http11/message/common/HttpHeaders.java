package org.apache.coyote.http11.message.common;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String NO_CONTENT_LENGTH = "0";
    private static final String NO_VALUE = "";
    private static final String DELIMITER = ": ";
    private static final String NEWLINE = "\r\n";

    private final Map<HttpHeader, String> headers = new EnumMap<>(HttpHeader.class);

    public HttpHeaders() {
    }

    public HttpHeaders(Map<HttpHeader, String> headers) {
        this.headers.putAll(headers);
    }

    public void add(HttpHeader header, String value) {
        headers.put(header, value);
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(HttpHeader.CONTENT_LENGTH, NO_CONTENT_LENGTH);
        return Integer.parseInt(contentLength);
    }

    public String getCookies() {
        return headers.getOrDefault(HttpHeader.COOKIE, NO_VALUE);
    }

    public String getValue(HttpHeader header) {
        if (headers.containsKey(header)) {
            return headers.get(header);
        }
        throw new IllegalArgumentException(header.name() + "(은)는 header에 존재하지 않습니다.");
    }

    public String convertMessage() {
        return headers.keySet().stream()
                .map(key -> key.getDisplayName() + DELIMITER + headers.get(key))
                .collect(collectingAndThen(toList(), lines -> String.join(NEWLINE, lines)));
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                '}';
    }
}
