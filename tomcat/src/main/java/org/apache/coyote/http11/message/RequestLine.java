package org.apache.coyote.http11.message;

import java.util.Optional;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final String PATH_EXTENSION_DELIMITER = ".";
    private static final String PATH_DELIMITER = "/";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final int START_LINE_INFORMATION_COUNT = 3;
    private static final int PATH_INDEX_IN_URI = 0;

    private final HttpMethod method;
    private final String uri;
    private final HttpProtocol protocol;

    private RequestLine(
        final HttpMethod method,
        final String uri,
        final HttpProtocol protocol
    ) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public static RequestLine from(final String startLine) {
        final String[] parsedStartLine = parseStartLine(startLine);
        final String method = parsedStartLine[0];
        final String uri = parsedStartLine[1];
        final String protocol = parsedStartLine[2];

        return new RequestLine(HttpMethod.from(method), uri, HttpProtocol.from(protocol));
    }

    private static String[] parseStartLine(final String startLine) {
        final String[] parsedStartLine = startLine.split(DELIMITER);
        if (parsedStartLine.length != START_LINE_INFORMATION_COUNT) {
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
        return parsedStartLine;
    }

    public boolean isMatchingRequest(final HttpMethod method, final String path) {
        return this.method == method && getPath().equals(path);
    }

    public Optional<String> parseQueryString() {
        if (uri.contains(QUERY_STRING_DELIMITER)) {
            int index = uri.indexOf(QUERY_STRING_DELIMITER);
            return Optional.of(uri.substring(index + 1));
        }
        return Optional.empty();
    }

    public Optional<String> parseFileExtensionFromPath() {
        // TODO: 2023-09-04 Path 도메인으로 이동
        final String path = getPath();
        if (!path.contains(PATH_EXTENSION_DELIMITER)) {
            return Optional.empty();
        }
        final String[] parsedPath = path.split(PATH_DELIMITER);
        final String fileName = parsedPath[parsedPath.length - 1];
        final String[] parsedFileName = fileName.split("\\" + PATH_EXTENSION_DELIMITER);
        return Optional.of(parsedFileName[parsedFileName.length - 1]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        final String[] parsedRequestPath = uri.split("\\" + QUERY_STRING_DELIMITER);
        return parsedRequestPath[PATH_INDEX_IN_URI];
    }
}
