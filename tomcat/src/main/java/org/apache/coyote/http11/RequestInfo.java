package org.apache.coyote.http11;

public class RequestInfo {

    public static final String QUERY_STRING_CONDITION = "?";
    public static final String QUERY_STRING_DELIMITER = "\\?";

    private final HttpMethod httpMethod;
    private final String requestURI;
    private final HttpVersion httpVersion;

    public RequestInfo(final String request) {
        final String[] splitRequest = request.split(" ");
        this.httpMethod = HttpMethod.findHttpMethod(splitRequest[0]);
        this.requestURI = splitRequest[1];
        this.httpVersion = HttpVersion.findVersion(splitRequest[2]);
    }

    public String getParsedRequestURI() {
        String uri = requestURI;
        if (uri.contains(QUERY_STRING_CONDITION)) {
            uri = uri.split(QUERY_STRING_DELIMITER)[0];
        }
        return uri;
    }

    public boolean isSameParsedRequestURI(final String uri) {
        return getParsedRequestURI().equals(uri);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
