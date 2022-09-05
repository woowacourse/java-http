package org.apache.coyote.http11.support;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.web.QueryParameters;
import java.util.Collections;
import java.util.Objects;

public class HttpStartLine {

    private static final int START_LINE_SIZE = 3;
    private static final int STATIC_RESOURCE_URI_SIZE = 1;
    private static final String QUERY_STRING_PREFIX = "\\?";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int URI_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;

    private final HttpMethod httpMethod;
    private final String uri;
    private final QueryParameters queryParameters;
    private final HttpVersion httpVersion;

    public HttpStartLine(final HttpMethod httpMethod, final String uri, final QueryParameters queryParameters, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.httpVersion = httpVersion;
    }

    public HttpStartLine(final HttpMethod httpMethod, final String uri, final HttpVersion httpVersion) {
        this(httpMethod, uri, null, httpVersion);
    }


    public static HttpStartLine from(final String[] startLine) {
        validateStartLineSize(startLine);
        final HttpMethod httpMethod = HttpMethod.from(startLine[HTTP_METHOD_INDEX]);
        final String[] splitUri = startLine[URL_INDEX].split(QUERY_STRING_PREFIX);
        final HttpVersion httpVersion = HttpVersion.from(startLine[HTTP_VERSION_INDEX]);

        if (splitUri.length == STATIC_RESOURCE_URI_SIZE) {
            return new HttpStartLine(httpMethod, splitUri[URI_INDEX], httpVersion);
        }

        final QueryParameters queryParameters = QueryParameters.from(splitUri[QUERY_STRING_INDEX]);
        return new HttpStartLine(httpMethod, splitUri[URI_INDEX], queryParameters, httpVersion);
    }

    private static void validateStartLineSize(final String[] startLine) {
        if (startLine.length != START_LINE_SIZE) {
            throw new InvalidHttpRequestException();
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public QueryParameters getQueryParameters() {
        if (Objects.isNull(queryParameters)) {
            return new QueryParameters(Collections.emptyMap());
        }
        return queryParameters;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpStartLine)) return false;
        final HttpStartLine that = (HttpStartLine) o;
        return httpMethod == that.httpMethod && Objects.equals(uri, that.uri) && Objects.equals(queryParameters, that.queryParameters) && httpVersion == that.httpVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri, queryParameters, httpVersion);
    }

    @Override
    public String toString() {
        return "HttpStartLine{" +
                "httpMethod=" + httpMethod +
                ", uri='" + uri + '\'' +
                ", queryParameters=" + queryParameters +
                ", httpVersion=" + httpVersion +
                '}';
    }
}
