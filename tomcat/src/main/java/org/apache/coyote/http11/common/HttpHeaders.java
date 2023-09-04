package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.util.Parser;

public class HttpHeaders {
    private Map<String, String> headers;

    public HttpHeaders() {
        headers = new LinkedHashMap<>();
    }

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders create(final List<String> lines) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String line : lines) {
            String[] split = line.split(": ");
            headers.put(split[0], split[1]);
        }

        return new HttpHeaders(headers);
    }

    public void addHeader(HttpHeaderName header, String value) {
        headers.put(header.getName(), value); // map의 key로 string? httpheader?
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Cookie getCookie() {
        String cookieLine = headers.get("Cookie");
        if (cookieLine == null) {
            throw new IllegalArgumentException("Cookie가 존재하지 않습니다");
        }
        return Parser.parseToCookie(cookieLine);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
