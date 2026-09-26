package org.apache.coyote.http11;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int TOKEN_COUNT = 3;

    private final HttpMethod method;
    private final String path;
    private final String version;

    public RequestLine(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }

        String[] tokens = line.trim().split(DELIMITER);
        if (tokens.length != TOKEN_COUNT) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다: " + line);
        }

        this.method = HttpMethod.from(tokens[0]);
        this.path = parsePath(tokens[1]);
        this.version = tokens[2];
    }

    private String parsePath(String requestUri) {
        String path = requestUri;
        int queryStartIndex = path.indexOf("?");
        if (queryStartIndex != -1) {
            path = path.substring(0, queryStartIndex);
        }

        if (!path.contains(".")) {
            path = path.concat(".html");
        }

        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
