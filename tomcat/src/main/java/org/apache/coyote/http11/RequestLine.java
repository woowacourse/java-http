package org.apache.coyote.http11;

import java.io.IOException;

public class RequestLine {
    private static final String START_LINE_DELIMITER = " ";
    private final HttpMethod httpMethod;
    private final HttpPath httpPath;
    private final HttpVersion httpVersion;

    public RequestLine(HttpMethod httpMethod, HttpPath httpPath, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.httpPath = httpPath;
        this.httpVersion = httpVersion;
    }

    public RequestLine(String line) throws IOException {
        String[] params = line.split(START_LINE_DELIMITER);

        httpMethod = HttpMethod.toValue(params[0]);
        httpPath = new HttpPath(params[1]);
        httpVersion = HttpVersion.getByString(params[2]);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
