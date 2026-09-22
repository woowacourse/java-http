package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> values;

    public HttpHeaders(List<String> headerLines) {
        this.values = Map.copyOf(parseHeaders(headerLines));
    }

    private Map<String, String> parseHeaders(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();

        for (String line : headerLines) {
            String[] parts = line.split(":", 2);

            if (parts.length != 2 || parts[0].isBlank()) {
                throw new IllegalArgumentException("헤더 형식이 올바르지 않습니다.");
            }

            String name = parts[0].trim().toLowerCase(Locale.ROOT);
            String value = parts[1].trim();

            headers.put(name, value);
        }

        return headers;
    }

    public String get(String name) {
        return values.get(name.toLowerCase(Locale.ROOT));
    }

    public int getContentLength() {
        String value = get("Content-Length");

        if (value == null) {
            return 0;
        }

        int contentLength = Integer.parseInt(value);

        if (contentLength < 0) {
            throw new IllegalArgumentException("본문 길이는 음수일 수 없습니다.");
        }

        return contentLength;
    }
}
