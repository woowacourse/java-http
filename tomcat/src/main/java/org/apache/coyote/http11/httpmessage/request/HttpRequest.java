package org.apache.coyote.http11.httpmessage.request;

import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.support.HttpRequestParser;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String path;
    private final String protocol;
    private final HttpHeader header;
    private final QueryString queryString;
    private final RequestBody requestBody;

    public HttpRequest(
        final HttpMethod httpMethod,
        final String path,
        final String protocol,
        final HttpHeader header,
        final QueryString queryString,
        final RequestBody requestBody
    ) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocol = protocol;
        this.header = header;
        this.queryString = queryString;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final String request) {
        return new HttpRequest(
            HttpRequestParser.getHttpMethod(request),
            HttpRequestParser.getPath(request),
            HttpRequestParser.getProtocol(request),
            HttpRequestParser.getHeader(request),
            HttpRequestParser.getQueryString(request),
            HttpRequestParser.getRequestBody(request)
        );
    }

    public boolean hasBlankRegisterUserBody() {
        return requestBody.getAccount().isBlank()
            || requestBody.getEmail().isBlank()
            || requestBody.getPassword().isBlank();
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public String getAuthCookie() {
        return header.getCookies().getAuthCookie();
    }

    public RequestBody getBody() {
        return requestBody;
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
