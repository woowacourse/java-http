package org.apache.coyote.http11.common;

public class RequestURI {

    private final String uri;
    private final QueryString queryString;
    private final String httpMethod;
    private final String httpVersion;

    private RequestURI(
            final String uri,
            final QueryString queryString,
            final String httpMethod,
            final String httpVersion
    ) {
        this.uri = uri;
        this.queryString = queryString;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
    }

    public static RequestURI of(
            final String uri,
            final QueryString queryString,
            final String httpMethod,
            final String httpVersion
    ) {
        return new RequestURI(uri, queryString, httpMethod, httpVersion);
    }

    public String getUri() {
        return uri;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

}
