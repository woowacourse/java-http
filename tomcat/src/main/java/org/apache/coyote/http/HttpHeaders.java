package org.apache.coyote.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class HttpHeaders {

    private static final String DELIMITER = ":";

    private final Map<String, String> values;

    private HttpHeaders(Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(List<String> lines) {
        final Map<String, String> values = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String line : lines) {
            final int index = line.indexOf(DELIMITER);
            if (index <= 0) {
                throw new IllegalArgumentException("잘못된 헤더 형식입니다: " + line);
            }

            final String name = line.substring(0, index).trim();
            final String value = line.substring(index + 1).trim();

            validateHeaderContent(name, value);
            values.put(name, value);
        }
        return new HttpHeaders(values);
    }

    public static HttpHeaders from(Map<String, String> headers) {
        return new HttpHeaders(headers);
    }

    public Map<String, String> getValues() {
        return values;
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public int contentLength() {
        return get("Content-Length")
                .map(Integer::parseInt)
                .orElse(0);
    }

    public String contentType() {
        return get("Content-Type").orElse("");
    }


    private static void validateHeaderContent(String name, String value) {
        if (name.equalsIgnoreCase("Content-Length")) {
            validateContentLength(value);
        }
    }

    private static void validateContentLength(String value) {
        try {
            final int length = Integer.parseInt(value);
            if (length < 0) {
                throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다: " + value);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length입니다: " + value);
        }
    }
}
