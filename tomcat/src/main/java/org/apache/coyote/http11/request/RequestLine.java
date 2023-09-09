package org.apache.coyote.http11.request;

import java.util.Objects;

public class RequestLine {

    private static final String TOKEN_DELIMITER = " ";

    private final String httpMethod;
    private final String path;
    private final String httpVersion;

    private RequestLine(String httpMethod, String path, String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] splitStatusLine = Objects.requireNonNull(requestLine.trim()).split(TOKEN_DELIMITER);
        String httpMethod = splitStatusLine[0];
        String path = splitStatusLine[1];
        String httpVersion = splitStatusLine[2];
        return new RequestLine(httpMethod, path, httpVersion);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
