package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestHeaders {

    private static final String HEADER_SEPARATOR = System.lineSeparator();
    private static final String HEADER_KEY_VALUE_SPLIT = ":";
    private static final String COOKIE = "Cookie";
    private static final String COOKIE_SPLITER = ";";
    private static final String COOKIE_VALUE_SPLITER = "=";
    Map<String, String> headers;

    public RequestHeaders(String headers) {
        this.headers = extractHeader(headers);
    }

    private Map<String, String> extractHeader(String headers) {
        return Stream.of(headers.split(HEADER_SEPARATOR))
            .collect(Collectors.toMap(
                this::keyOf,
                this::valueOf
            ));
    }

    private String keyOf(String header) {
        String[] split = header.split(HEADER_KEY_VALUE_SPLIT);
        return split[0].trim();
    }

    private String valueOf(String header) {
        String[] split = header.split(HEADER_KEY_VALUE_SPLIT);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < split.length; i++) {
            stringBuilder.append(split[i]);
        }
        return stringBuilder.toString().trim();
    }

    public Optional<Cookies> getCookie() {
        if (!headers.containsKey(COOKIE)) {
            return Optional.empty();
        }
        return Optional.of(new Cookies(extractCookie(headers.get(COOKIE))));
    }

    private Map<String, String> extractCookie(String cookies) {
        return Stream.of(cookies.split(COOKIE_SPLITER))
            .collect(Collectors.toMap(
                this::extractKey,
                this::extractValue
            ));
    }

    private String extractKey(String value) {
        String[] split = value.split(COOKIE_VALUE_SPLITER);
        return split[0].trim();
    }

    private String extractValue(String value) {
        String[] split = value.split(COOKIE_VALUE_SPLITER);
        return split[1].trim();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
