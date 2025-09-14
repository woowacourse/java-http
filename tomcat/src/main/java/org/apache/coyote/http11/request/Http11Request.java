package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class Http11Request {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestCookies requestCookies;
    private final RequestBodyParams requestBodyParams;

    public Http11Request(
            final RequestLine requestLine,
            final RequestHeaders requestHeaders,
            final RequestBodyParams requestBodyParams
    ) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestCookies = new RequestCookies();
        this.requestCookies.parseCookies(requestHeaders.getHeader("cookie"));
        this.requestBodyParams = requestBodyParams;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParam(final String queryParamName) {
        return requestLine.getQueryParam(queryParamName);
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getHeader(String headerName) {
        return requestHeaders.getHeader(headerName);
    }

    public String getBodyParam(String bodyParamName) {
        return requestBodyParams.getBodyParam(bodyParamName);
    }

    public String getCookie(String cookieName) {
        return requestCookies.getCookie(cookieName);
    }
}
