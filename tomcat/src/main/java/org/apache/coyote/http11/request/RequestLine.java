package org.apache.coyote.http11.request;

import java.util.Objects;

import org.apache.coyote.http11.HttpMethod;

public class RequestLine {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_SIZE = 3;
    private static final int METHOD_POSITION = 0;
    private static final int URI_POSITION = 1;
    private static final int VERSION_POSITION = 2;

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    public RequestLine(HttpMethod method, String uri, HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine from(String line) {
        if (Objects.nonNull(line) && !line.isEmpty()) {
            String[] requestLine = line.split(REQUEST_LINE_DELIMITER);
            if (requestLine.length >= REQUEST_LINE_SIZE) {
                return new RequestLine(HttpMethod.of(requestLine[METHOD_POSITION]), requestLine[URI_POSITION], HttpVersion.of(requestLine[VERSION_POSITION]));
            }
        }
        throw new IllegalArgumentException();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
