package org.apache.coyote.request;

import java.util.HashMap;
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
                .orElseThrow(() -> new IllegalArgumentException("http 요청을 읽을 수 없습니다."));
    }

    private static Map<String, Object> extractHeaders(String rawRequest) {
        final int headerStart = rawRequest.indexOf("\r\n");
        final int headerEnd = rawRequest.indexOf("\r\n\r\n");
        final Map<String, Object> headers = new HashMap<>();

        if (headerStart == headerEnd) {
            return headers;
        }

        rawRequest.substring(headerStart + 2, headerEnd)
                .lines()
                .map(line -> line.split(":\\s+"))
                .forEach(fieldNameValue -> headers.put(fieldNameValue[0], fieldNameValue[1]));
        return headers;
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
