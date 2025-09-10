package org.apache.coyote.http11.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {
    public static final String VALID_HEADER_KEY_PATTERN = "^[A-Za-z0-9-]+$";
    public static final String VALID_HEADER_VALUE_PATTERN = ".*[\\r\\n\\x00-\\x1F\\x7F].*";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private HttpHeaders() {
    }

    public static HttpHeaders init() {
        return new HttpHeaders();
    }

    public static HttpHeaders fromLines(List<String> lines) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            String[] parts = line.split(":", 2);
            validateHeader(line, parts);
            String name = parts[0].trim();
            String value = parts[1].trim();
            httpHeaders.add(name, value);
        }
        return httpHeaders;
    }

    public void add(String name, String value) {
        headers.computeIfAbsent(sanitize(name), key -> new ArrayList<>()).add(sanitize(value));
    }

    public boolean hasContentLength() {
        return contains(CONTENT_LENGTH);
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public List<String> get(String name) {
        return new ArrayList<>(headers.getOrDefault(name, Collections.emptyList()));
    }

    public String getFirst(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.getFirst();
    }

    public int getContentLength() {
        if (hasContentLength()) {
            return Integer.parseInt(getFirst(CONTENT_LENGTH));
        }
        return 0;
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        headers.forEach((name, values) -> {
            for (String value : values) {
                lines.add(name + ": " + value);
            }
        });
        return lines;
    }

    private static void validateHeader(String line, String[] parts) {
        if (parts.length != 2 || parts[0] == null || parts[1] == null) {
            throw new IllegalArgumentException("유효하지 않은 헤더: " + line);
        }

        validateHeaderKey(parts);
        validateHeaderValue(parts);
    }

    private static void validateHeaderKey(String[] parts) {
        if (!parts[0].matches(VALID_HEADER_KEY_PATTERN)) {
            throw new IllegalArgumentException("잘못된 헤더 이름: " + parts[0]);
        }
    }

    private static void validateHeaderValue(String[] parts) {
        if (parts[1].matches(VALID_HEADER_VALUE_PATTERN)) {
            throw new IllegalArgumentException("잘못된 헤더 값: " + parts[1]);
        }
    }

    // 헤더 인젝션 방지용 메서드
    private static String sanitize(String value) {
        if (value == null) {
            throw new IllegalArgumentException("헤더의 키/값이 null입니다");
        }

        return value.replaceAll("[\\r\\n]", "");
    }
}
