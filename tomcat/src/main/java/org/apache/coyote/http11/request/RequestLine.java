package org.apache.coyote.http11.request;

class RequestLine {

    private static final String DELIMITER = " ";
    private static final String QUERY_DELIMITER = "?";
    private static final int TOKEN_COUNT = 3;

    private final HttpMethod method;
    private final String path;

    private RequestLine(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    static RequestLine from(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }

        String[] tokens = line.trim().split(DELIMITER);
        if (tokens.length != TOKEN_COUNT) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다: " + line);
        }

        return new RequestLine(HttpMethod.from(tokens[0]), pathOf(tokens[1]));
    }

    private static String pathOf(String requestUri) {
        int queryStartIndex = requestUri.indexOf(QUERY_DELIMITER);
        if (queryStartIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStartIndex);
    }

    boolean hasMethod(HttpMethod method) {
        return this.method == method;
    }

    String getPath() {
        return path;
    }
}
