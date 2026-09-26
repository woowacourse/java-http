package org.apache.coyote.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestParser {

    public static MyHttpRequest parse(String rawRequest) {
        RequestLine requestLine = RequestLine.from(extractRequestLine(rawRequest));
        Map<String, Object> headerFields = extractHeaders(rawRequest);
        String body = extractBody(rawRequest);

        return new MyHttpRequest(
                requestLine,
                headerFields,
                body
        );
    }

    private static String extractRequestLine(String rawRequest) {
        return rawRequest.lines()
                .findFirst()
                .orElseThrow(() -> new MalformedRequestException("http 요청 라인을 읽을 수 없습니다."));
    }

    private static Map<String, Object> extractHeaders(String rawRequest) {
        final int headerStart = rawRequest.indexOf("\r\n");
        final int headerEnd = rawRequest.indexOf("\r\n\r\n");
        final Map<String, Object> headers = new LinkedHashMap<>();

        if (headerStart == headerEnd) {
            return headers;
        }

        rawRequest.substring(headerStart + 2, headerEnd)
                .lines()
                .forEach(line -> addHeader(headers, line));
        return headers;
    }

    private static void addHeader(Map<String, Object> headers, String line) {
        int separatorIndex = line.indexOf(':');
        if (separatorIndex <= 0) {
            throw new IllegalArgumentException("잘못된 HTTP 헤더입니다: " + line);
        }
        String name = line.substring(0, separatorIndex).strip();
        String value = line.substring(separatorIndex + 1).strip();
        headers.put(name, value);
    }

    private static String extractBody(String rawRequest) {
        final String bodySeparator = "\r\n\r\n";
        int startIndexOfBody = rawRequest.indexOf(bodySeparator);
        if (startIndexOfBody == -1) {
            return "";
        }
        return rawRequest.substring(startIndexOfBody + bodySeparator.length());
    }

    private HttpRequestParser() {
    }
}
