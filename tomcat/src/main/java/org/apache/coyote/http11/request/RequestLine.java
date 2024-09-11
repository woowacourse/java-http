package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final String QUERY_PARAMETER_REGEX = "\\?";

    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(REQUEST_LINE_DELIMITER);
        this.httpMethod = HttpMethod.from(parts[0]);
        this.uri = parts[1];
        this.httpVersion = HttpVersion.from(parts[2]);
    }

    public boolean isGetMethod() {
        return httpMethod.isGet();
    }

    public boolean isPostMethod() {
        return httpMethod.isPost();
    }

    public boolean hasQueryParameter() {
        return uri.contains(QUERY_PARAMETER_DELIMITER);
    }

    public QueryParameter getQueryParameter() {
        String queryParameter = uri.split(QUERY_PARAMETER_REGEX)[1];
        return new QueryParameter(queryParameter);
    }

    public String getPath() {
        return uri.split(QUERY_PARAMETER_REGEX)[0];
    }

    public String getUri() {
        return uri;
    }
}
