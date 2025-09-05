package org.apache.coyote.http11.http.common.header;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.http.common.HttpSplitFormat;

public class HttpHeader {

    private final Map<String, String> httpHeaderInfo;

    private HttpHeader(final BufferedReader bufferedReader) throws IOException {
        this.httpHeaderInfo = createHeaderInfo(bufferedReader);
    }

    private HttpHeader(final Map<String, String> httpHeaderInfo) {
        this.httpHeaderInfo = Map.copyOf(httpHeaderInfo);
    }

    public static HttpHeader from(final BufferedReader bufferedReader) throws IOException {
        validateNull(bufferedReader);
        return new HttpHeader(bufferedReader);
    }

    public static HttpHeader from(final Map<String, String> httpHeaderInfo) {
        validateNull(httpHeaderInfo);
        return new HttpHeader(httpHeaderInfo);
    }

    private static void validateNull(final BufferedReader bufferedReader) {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("bufferedReader는 null일 수 없습니다.");
        }
    }

    private static void validateNull(final Map<String, String> httpHeaderInfo) {
        if (httpHeaderInfo == null) {
            throw new IllegalArgumentException("httpHeaderInfo는 null일 수 없습니다.");
        }
    }

    private static void validateSplitFormat(final String requestPayload, final int headerSplitIndex) {
        if (headerSplitIndex == -1 || headerSplitIndex == requestPayload.length() - 1) {
            throw new IllegalArgumentException(
                    "유효하지 읺은 header 형식입니다: requestPayload=%s, headerSplitIndex=%d".formatted(requestPayload,
                            headerSplitIndex));
        }
    }

    private Map<String, String> createHeaderInfo(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = readHeaderLines(bufferedReader);
        return parseHeaderLines(headerLines);
    }

    private List<String> readHeaderLines(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String headerLine = null;
        while ((headerLine = bufferedReader.readLine()) != null) {
            if ("".equals(headerLine)) {
                break;
            }
            headerLines.add(headerLine);
        }
        return headerLines;
    }

    private Map<String, String> parseHeaderLines(final List<String> httpHeaderLines) {
        final Map<String, String> httpHeaderInfo = new HashMap<>();
        for (final String requestPayload : httpHeaderLines) {
            final int headerSplitIndex = requestPayload.indexOf(HttpSplitFormat.HEADER.getValue());
            validateSplitFormat(requestPayload, headerSplitIndex);

            final String headerKey = requestPayload.substring(0, headerSplitIndex).trim().toLowerCase();
            final String headerValue = requestPayload.substring(headerSplitIndex + 1).trim();

            validateHeaderFormat(headerKey, headerValue);
            httpHeaderInfo.put(headerKey, headerValue);
        }
        return httpHeaderInfo;
    }

    private void validateHeaderFormat(final String headerKey, final String headerValue) {
        if (headerKey == null || headerValue == null) {
            throw new IllegalArgumentException(
                    "header key와 value는 null일 수 없습니다: headerKey=%s, headerValue=%s".formatted(headerKey, headerValue));
        }
        if (headerKey.isBlank()) {
            throw new IllegalArgumentException("header의 key값은 빈 값일 수 없습니다");
        }
    }

    public boolean containsKey(String target) {
        return httpHeaderInfo.containsKey(target);
    }

    public String getValue(final String target) {
        final String cleanTarget = target.trim().toLowerCase();
        if (!httpHeaderInfo.containsKey(cleanTarget)) {
            throw new IllegalArgumentException("존재하지 않는 header입니다: %s".formatted(target));
        }
        return httpHeaderInfo.get(target);
    }

    public List<String> getFormat() {
        return httpHeaderInfo.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .toList();
    }
}
