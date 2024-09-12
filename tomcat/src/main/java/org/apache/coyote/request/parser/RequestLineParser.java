package org.apache.coyote.request.parser;

import java.util.List;

public class RequestLineParser {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int REQUEST_PROTOCOL = 2;
    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final int STANDARD_REQUEST_LINE_LENGTH = 3;

    private final List<String> requestLines;

    public RequestLineParser(String requestLines) {
        this.requestLines = splitToRequestLines(requestLines);
    }

    private List<String> splitToRequestLines(String requestLine) {
        List<String> requestLines = List.of(requestLine.split(REQUEST_LINE_SEPARATOR));

        if (requestLines.size() != STANDARD_REQUEST_LINE_LENGTH) {
            throw new IllegalArgumentException("HttpLine의 형식이 옳바르지 않습니다.");
        }
        return requestLines;
    }

    public String parseRequestPath() {
        return requestLines.get(REQUEST_PATH_INDEX).trim();
    }

    public String parseRequestMethod() {
        return requestLines.get(REQUEST_METHOD_INDEX).trim();
    }

    public String parseRequestProtocol() {
        return requestLines.get(REQUEST_PROTOCOL);
    }
}
