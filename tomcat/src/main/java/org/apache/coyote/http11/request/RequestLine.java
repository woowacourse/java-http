package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.exception.InvalidRequestLineException;

public class RequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String DELIMITER = " ";
    private static final int VALID_REQUEST_LINE_SIZE = 3;
    private static final String QUERY_STRING_BEGIN = "?";
    private static final int EMPTY_QUERY_STRING = -1;

    private final HttpMethod httpMethod;
    private final String uri;
    private final String httpVersion;

    private RequestLine(final HttpMethod httpMethod, final String uri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String line) {
        final String[] requestLine = line.split(DELIMITER);
        validate(requestLine);
        return new RequestLine(
                HttpMethod.from(requestLine[HTTP_METHOD_INDEX]),
                requestLine[URI_INDEX],
                requestLine[HTTP_VERSION_INDEX]
        );
    }

    private static void validate(final String[] requestLine) {
        if (requestLine.length != VALID_REQUEST_LINE_SIZE) {
            throw new InvalidRequestLineException();
        }
    }

    public String parseUriWithOutQueryString() {
        int queryStringIndex = uri.indexOf(QUERY_STRING_BEGIN);
        if (queryStringIndex == EMPTY_QUERY_STRING) {
            return uri;
        }
        return uri.substring(0, queryStringIndex);
    }

    public QueryString parseQueryString() {
        return QueryString.from(uri);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
