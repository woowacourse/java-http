package org.apache.coyote.http11.http.common.header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.http.common.HttpSplitFormat;

public class HttpHeader {

    private final Map<String, List<String>> httpHeaderInfo;

    private HttpHeader(final Map<String, List<String>> httpHeaderInfo) {
        this.httpHeaderInfo = new HashMap<>(httpHeaderInfo);
    }

    public static HttpHeader from(final List<String> headerLines) {
        validateNull(headerLines);
        return new HttpHeader(parseHeaderLines(headerLines));
    }

    public static HttpHeader from(final Map<String, List<String>> httpHeaderInfo) {
        validateNull(httpHeaderInfo);
        return new HttpHeader(httpHeaderInfo);
    }

    private static void validateNull(final List<String> headerLines) {
        if (headerLines == null) {
            throw new IllegalArgumentException("headerLines는 null일 수 없습니다.");
        }
    }

    private static void validateNull(final Map<String, List<String>> httpHeaderInfo) {
        if (httpHeaderInfo == null) {
            throw new IllegalArgumentException("httpHeaderInfo는 null일 수 없습니다.");
        }
    }

    private static Map<String, List<String>> parseHeaderLines(final List<String> httpHeaderLines) {
        final Map<String, List<String>> httpHeaderInfo = new HashMap<>();

        for (final String requestPayload : httpHeaderLines) {
            final String[] parts = requestPayload.split(HttpSplitFormat.HEADER.getValue(), 2);

            if (parts.length != 2) {
                throw new IllegalArgumentException("유효하지 않은 header 형식입니다: requestPayload=" + requestPayload);
            }

            final String headerKey = parts[0].trim().toLowerCase();
            final String headerValue = parts[1].trim();

            validateHeaderFormat(headerKey, headerValue);
            httpHeaderInfo.computeIfAbsent(headerKey, k -> new ArrayList<>()).add(headerValue);
        }
        return httpHeaderInfo;
    }

    private static void validateHeaderFormat(final String headerKey, final String headerValue) {
        if (headerKey == null || headerValue == null) {
            throw new IllegalArgumentException(
                    "header key와 value는 null일 수 없습니다: headerKey=%s, headerValue=%s".formatted(headerKey, headerValue));
        }
        if (headerKey.isBlank()) {
            throw new IllegalArgumentException("header의 key값은 빈 값일 수 없습니다");
        }
    }

    public void addHeader(final String headerKey, final String headerValue) {
        if (headerKey == null) {
            throw new IllegalArgumentException("header key는 null일 수 없습니다");
        }
        if (headerValue == null) {
            throw new IllegalArgumentException("header value는 null일 수 없습니다");
        }
        httpHeaderInfo.computeIfAbsent(headerKey.trim().toLowerCase(), k -> new ArrayList<>()).add(headerValue);
    }

    public Optional<String> getFirstValue(final String target) {
        final String cleanTarget = target.trim().toLowerCase();
        if (!httpHeaderInfo.containsKey(cleanTarget)) {
            return Optional.empty();
        }
        List<String> values = httpHeaderInfo.get(cleanTarget);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(values.getFirst());
    }

    public List<String> getValues(final String target) {
        final String cleanTarget = target.trim().toLowerCase();
        return httpHeaderInfo.getOrDefault(cleanTarget, new ArrayList<>());
    }

    public List<String> getFormat() {
        return httpHeaderInfo.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> entry.getKey() + ": " + value))
                .toList();
    }
}
