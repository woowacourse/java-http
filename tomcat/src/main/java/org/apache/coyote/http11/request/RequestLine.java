package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.Http11Exception;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String DELIMITER = " ";
    private static final int VALID_REQUEST_LINE_SIZE = 3;

    private final HttpMethod httpMethod;
    private final Path path;
    private final HttpVersion httpVersion;

    private RequestLine(final HttpMethod httpMethod, final Path path, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String line) {
        final String[] requestLine = line.split(DELIMITER);
        validate(requestLine);
        return new RequestLine(
                HttpMethod.from(requestLine[HTTP_METHOD_INDEX]),
                Path.from(requestLine[URI_INDEX]),
                HttpVersion.from(requestLine[HTTP_VERSION_INDEX])
        );
    }

    private static void validate(final String[] requestLine) {
        if (requestLine.length != VALID_REQUEST_LINE_SIZE) {
            throw new Http11Exception("올바르지 않은 RequestLine 형식입니다.");
        }
    }

    public String parseUri() {
        return path.parseUri();
    }

    public QueryString parseQueryString() {
        return path.parseQueryString();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
