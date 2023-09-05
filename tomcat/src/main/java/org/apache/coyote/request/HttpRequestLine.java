package org.apache.coyote.request;

import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.exception.http.InvalidStartLineException;

public class HttpRequestLine {

    private static final String DELIMITER = " ";
    private static final int ELEMENT_COUNT = 3;

    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final HttpVersion httpVersion;

    private HttpRequestLine(final HttpMethod httpMethod, final RequestPath requestPath, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine parse(final String line) {
        final String[] startLines = getStartLines(line);
        return new HttpRequestLine(
                HttpMethod.from(startLines[0]),
                RequestPath.from(startLines[1]),
                HttpVersion.from(startLines[2])
        );
    }

    private static String[] getStartLines(final String line) {
        if (line == null || line.isEmpty()) {
            throw new InvalidStartLineException("Start Line is Null Or Empty");
        }
        return splitStartLines(line);
    }

    private static String[] splitStartLines(final String line) {
        final String[] startLines = line.split(DELIMITER);
        if (startLines.length != ELEMENT_COUNT) {
            throw new InvalidStartLineException("Start Line Element Count Not Match");
        }
        return startLines;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUriPath() {
        return requestPath.getUriPath();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
