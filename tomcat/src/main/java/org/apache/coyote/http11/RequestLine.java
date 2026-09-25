package org.apache.coyote.http11;

import java.io.IOException;

public class RequestLine {
    private static final String START_LINE_DELIMITER = " ";
    private final HttpMethod httpMethod;
    private final HttpPath httpPath;
    private final HttpVersion httpVersion;

    public RequestLine(String line) throws IOException {
        String[] params = line.split(START_LINE_DELIMITER);

        httpMethod = HttpMethod.from(params[0]);
        httpPath = new HttpPath(params[1]);
        httpVersion = HttpVersion.from(params[2]);
    }

    public boolean hasSamePath(String path) {
        return httpPath.startsWith(path);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
