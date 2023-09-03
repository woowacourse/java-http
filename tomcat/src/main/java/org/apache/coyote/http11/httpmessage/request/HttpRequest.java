package org.apache.coyote.http11.httpmessage.request;

import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.support.HttpRequestParser;

public class HttpRequest {

    final HttpMethod httpMethod;
    final String path;
    final String protocol;
    final HttpHeader header;
    final QueryString queryString;

    private HttpRequest(
        final HttpMethod httpMethod,
        final String path,
        final String protocol,
        final HttpHeader header,
        final QueryString queryString
    ) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocol = protocol;
        this.header = header;
        this.queryString = queryString;
    }

    public static HttpRequest from(final String request) {
        return new HttpRequest(
            HttpRequestParser.getHttpMethod(request),
            HttpRequestParser.getPath(request),
            HttpRequestParser.getProtocol(request),
            HttpRequestParser.getHeader(request),
            HttpRequestParser.getQueryString(request)
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
