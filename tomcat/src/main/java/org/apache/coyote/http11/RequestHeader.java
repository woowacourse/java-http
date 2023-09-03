package org.apache.coyote.http11;

import java.util.List;

public class RequestHeader {

    private static final String DELIMITER = " ";

    private final List<String> headers;
    private final HttpMethod httpMethod;

    private RequestHeader(final List<String> headers, final HttpMethod httpMethod) {
        this.headers = headers;
        this.httpMethod = httpMethod;
    }

    public static RequestHeader from(final List<String> requestHeader) {
        final String httpMethodString = requestHeader.get(0).split(DELIMITER)[0];
        final HttpMethod httpMethod = HttpMethod.findHttpMethod(httpMethodString);
        return new RequestHeader(requestHeader, httpMethod);
    }

    public String getParsedRequestURI() {
        String requestURI = getOriginRequestURI();
        if (requestURI.contains("?")) {
            requestURI = requestURI.split("\\?")[0];
        }
        return requestURI;
    }

    public String getOriginRequestURI() {
        final String firstLineOfRequestHeaders = headers.get(0);
        final String[] splitFirstLine = firstLineOfRequestHeaders.split(" ");
        return splitFirstLine[1];
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public boolean isSameParsedRequestURI(final String uri) {
        return getParsedRequestURI().equals(uri);
    }
}
