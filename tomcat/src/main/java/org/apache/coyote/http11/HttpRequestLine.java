package org.apache.coyote.http11;

public class HttpRequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final int NON_EXIST = -1;

    private final HttpMethod method;
    private final String requestUri;
    private final String protocol;

    public HttpRequestLine(final String requestLine) {
        final String[] requestLineValues = requestLine.split(REQUEST_LINE_DELIMITER);
        this.method = parseHttpMethod(requestLineValues);
        this.requestUri = parseRequestUri(requestLineValues);
        this.protocol = parseProtocol(requestLineValues);
    }

    private HttpMethod parseHttpMethod(final String[] values) {
        return HttpMethod.valueOf(values[HTTP_METHOD_INDEX]);
    }

    private String parseRequestUri(final String[] values) {
        final String path = values[REQUEST_URI_INDEX];
        final int queryStringBeginIndex = path.indexOf(QUERY_STRING_DELIMITER);
        if (queryStringBeginIndex == NON_EXIST) {
            return path;
        }
        return path.substring(0, queryStringBeginIndex);
    }

    private String parseProtocol(final String[] values) {
        return values[PROTOCOL_INDEX];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getProtocol() {
        return protocol;
    }
}
