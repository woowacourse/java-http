package org.apache.coyote.http11.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {
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
            String name = sanitize(parts[0].trim());
            String value = sanitize(parts[1].trim());
            httpHeaders.add(name, value);
        }
        return httpHeaders;
    }


    //TODO: 여기에도 sanitize 적용하기  (2025-09-7, 일, 17:22)
    // https://github.com/woowacourse/java-http/pull/800#discussion_r2326895285
    public void add(String name, String value) {
        headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
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


    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        headers.forEach((name, values) -> {
            for (String value : values) {
                lines.add(name + ": " + value);
            }
        });
        return lines;
    }

    //TODO: 헤더 유효성 검증 강화 필요  (2025-09-7, 일, 17:23)
    // https://github.com/woowacourse/java-http/pull/800#discussion_r2326895289
    private static void validateHeader(String line, String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("유효하지 않은 헤더: " + line);
        }
    }

    // 헤더 인젝션 방지용 메서드
    private static String sanitize(String str) {
        if (str == null) {
            throw new IllegalArgumentException("헤더의 키/값이 null입니다");
        }

        return str.replaceAll("[\\r\\n]", "");
    }
}
