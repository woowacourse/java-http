package org.apache.coyote.http11.handler.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequestMessage {

    private static final Pattern REQUEST_START_LINE_PATTERN = Pattern.compile("^\\S+ \\S+ \\S+$");
    private static final String REQUEST_START_LINE_DELIMITER = " ";
    private static final int REQUEST_START_LINE_ELEMENT_SIZE = 3;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final String HEADERS_LAST_LINE = "";

    private final String startLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequestMessage(final String startLine, final Map<String, String> headers,
        final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequestMessage with(final List<String> messageLines) {
        if (messageLines.isEmpty()) {
            throw new IllegalArgumentException("요청 메시지가 비어있습니다.");
        }

        final String startLine = extractStartLine(messageLines);
        final List<String> headers = extractHeaders(messageLines);
        final String body = extractBody(headers.size(), messageLines);

        return new HttpRequestMessage(startLine, parseHeaders(headers), body);
    }

    private static String extractStartLine(final List<String> messageLines) {
        final String firstLine = messageLines.get(0);
        if (REQUEST_START_LINE_PATTERN.matcher(firstLine.strip()).matches()) {
            return firstLine;
        }
        throw new IllegalArgumentException("start line 의 형식이 올바르지 않습니다. start line : " + firstLine);
    }

    private static List<String> extractHeaders(final List<String> messageLines) {
        final List<String> headers = new ArrayList<>();

        int headersLastLine = messageLines.indexOf(HEADERS_LAST_LINE);
        if (headersLastLine == -1) {
            headersLastLine = messageLines.size();
        }
        for (int index = 1; index < headersLastLine; index++) {
            headers.add(messageLines.get(index));
        }
        return headers;
    }

    private static String extractBody(final int headersSize, final List<String> messageLines) {
        if (headersSize + 1 < messageLines.size()) {
            return messageLines.subList(headersSize, messageLines.size())
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

    public static Map<String, String> parseHeaders(final List<String> headers) {
        final Map<String, String> parsedHeaders = new HashMap<>();

        headers.forEach(header -> {
            final int delimiterIndex = header.indexOf(":");
            final String headerName = header.substring(0, delimiterIndex).toLowerCase();
            final String headerValue = header.substring(delimiterIndex + 1).toLowerCase().strip();
            if (parsedHeaders.containsKey(headerName)) {
                throw new IllegalArgumentException("중복된 헤더가 존재합니다. Header : " + headerName);
            }
            parsedHeaders.put(headerName, headerValue);
        });

        return parsedHeaders;
    }

    public String getStartLine() {
        return startLine;
    }

    public String getTargetUrl() {
        final String[] startLineElements = startLine.split(REQUEST_START_LINE_DELIMITER);
        if (startLineElements.length != REQUEST_START_LINE_ELEMENT_SIZE) {
            throw new IllegalArgumentException("Invalid HTTP request start line: " + startLine);
        }
        return startLineElements[REQUEST_TARGET_INDEX];
    }

    public Optional<String> getHeader(final String header) {
        final String target = header.toLowerCase();

        if (!headers.containsKey(target)) {
            return Optional.empty();
        }
        return Optional.of(headers.get(target));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
