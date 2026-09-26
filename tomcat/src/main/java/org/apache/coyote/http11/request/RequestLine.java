package org.apache.coyote.http11.request;

class RequestLine {

    private static final String DELIMITER = " ";
    private static final String QUERY_DELIMITER = "?";
    private static final int TOKEN_COUNT = 3;

    private final HttpMethod method;
    private final String path;
    private final Parameters queryParameters;
    private final String version;

    private RequestLine(HttpMethod method, String path, Parameters queryParameters, String version) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.version = version;
    }

    static RequestLine from(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }

        String[] tokens = line.trim().split(DELIMITER);
        if (tokens.length != TOKEN_COUNT) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다: " + line);
        }

        String requestUri = tokens[1];
        int queryStartIndex = requestUri.indexOf(QUERY_DELIMITER);
        if (queryStartIndex == -1) {
            return new RequestLine(HttpMethod.from(tokens[0]), requestUri, Parameters.empty(), tokens[2]);
        }

        String path = requestUri.substring(0, queryStartIndex);
        Parameters queryParameters = Parameters.from(requestUri.substring(queryStartIndex + 1));
        return new RequestLine(HttpMethod.from(tokens[0]), path, queryParameters, tokens[2]);
    }

    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getQueryParameter(String name) {
        return queryParameters.get(name);
    }

    String getVersion() {
        return version;
    }
}
