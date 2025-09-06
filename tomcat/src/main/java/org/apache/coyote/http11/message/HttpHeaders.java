package org.apache.coyote.http11.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private HttpHeaders() {
    }

    public static HttpHeaders fromLines(List<String> lines) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                continue; // 잘못된 헤더는 스킵
            }
            String name = parts[0].trim();
            String value = parts[1].trim();
            httpHeaders.add(name, value);
        }
        return httpHeaders;
    }

    public void add(String name, String value) {
        headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }

    public List<String> get(String name) {
        return headers.getOrDefault(name, Collections.emptyList());
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
}
