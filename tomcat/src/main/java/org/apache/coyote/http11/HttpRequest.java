package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestHeader requestHeader;
    private final RequestBody requestBody;
    private final QueryString queryString;

    public HttpRequest(final RequestHeader requestHeader, final RequestBody requestBody,
                       final QueryString queryString) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.queryString = queryString;
    }

    public boolean isSameParsedRequestURI(final String uri) {
        return requestHeader.isSameParsedRequestURI(uri);
    }

    public String getParsedRequestURI() {
        return requestHeader.getParsedRequestURI();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public HttpMethod getHttpMethod() {
        return requestHeader.getHttpMethod();
    }

    public boolean hasCookie(final String cookie) {
        return requestHeader.hasCookie(cookie);
    }

    public String getCookieValue(final String key) {
        return requestHeader.getCookie(key);
    }
}
