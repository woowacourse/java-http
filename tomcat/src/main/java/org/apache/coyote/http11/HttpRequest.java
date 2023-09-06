package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    private final RequestHeader requestHeader;
    private final QueryString queryString;
    private final RequestBody requestBody;

    private HttpRequest(final RequestHeader requestHeader, final RequestBody requestBody,
                        final QueryString queryString) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.queryString = queryString;
    }

    public static HttpRequest of(final List<String> requestHeaderStrings, final String requestBodyString) {
        final RequestHeader requestHeader = RequestHeader.from(requestHeaderStrings);
        final RequestBody requestBody = RequestBody.from(requestBodyString);
        final QueryString queryString = QueryString.from(requestHeader.getOriginRequestURI());

        return new HttpRequest(requestHeader, requestBody, queryString);
    }

    public boolean isSameParsedRequestURI(final String uri) {
        return requestHeader.isSameParsedRequestURI(uri);
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.requestHeader.isSameHttpMethod(httpMethod);
    }

    public boolean hasCookie(final String cookie) {
        return requestHeader.hasCookie(cookie);
    }

    public String getCookieValue(final String key) {
        return requestHeader.getCookie(key);
    }

    public String getParsedRequestURI() {
        return requestHeader.getParsedRequestURI();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpMethod getHttpMethod() {
        return requestHeader.getHttpMethod();
    }

    public HttpVersion getHttpVersion() {
        return requestHeader.getHttpVersion();
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
