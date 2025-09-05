package org.apache.coyote.request.requestLine;

import java.util.List;

public class RequestLine {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;

    private static final int REQUEST_LINE_SIZE = 3;
    private static final String REQUEST_LINE_SEPARATOR = " ";

    private final RequestMethod requestMethod;
    private final RequestPath requestPath; //todo: requestPath, protocolVersion 객체 만들기
    private final String protocolVersion;

    public RequestLine(final String requestLine) {
        final List<String> requestLines = List.of(requestLine.split(REQUEST_LINE_SEPARATOR));
        validateRequestLines(requestLines);

        this.requestMethod = RequestMethod.from(requestLines.get(REQUEST_METHOD_INDEX));

        this.requestPath = RequestPath.from(requestLines.get(REQUEST_PATH_INDEX));
        this.protocolVersion = requestLines.get(PROTOCOL_VERSION_INDEX);
    }

    private void validateRequestLines(final List<String> requestLines) {
        if (requestLines.size() != REQUEST_LINE_SIZE) {
            throw new IllegalArgumentException("[ERROR] 요청 형식이 올바르지 않습니다");
        }
    }

    public boolean isSame(final RequestMethod requestMethod) {
        return this.requestMethod.equals(requestMethod);
    }

    public boolean isSame(final String requestPath) {
        return this.requestPath.isSame(requestPath);
    }

    public boolean isDefaultPath() {
        return this.requestPath.equals("/");
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public String getRequestPathExtension() {
        return this.requestPath.getRequestPathExtension();
    }
}
