package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.http11.session.Cookie;

public class RequestHeader {

    private final RequestInfo requestInfo;
    private final HttpHeader httpHeader;
    private final Cookie cookie;

    private RequestHeader(final RequestInfo requestInfo, final HttpHeader httpHeader, final Cookie cookie) {
        this.requestInfo = requestInfo;
        this.httpHeader = httpHeader;
        this.cookie = cookie;
    }

    public static RequestHeader from(final List<String> requestHeader) {
        final HttpHeader httpHeader = HttpHeader.of(requestHeader);
        final RequestInfo requestInfo = new RequestInfo(requestHeader.get(0));
        final Cookie cookie = Cookie.from(httpHeader);
        return new RequestHeader(requestInfo, httpHeader, cookie);
    }

    public String getParsedRequestURI() {
        return requestInfo.getParsedRequestURI();
    }

    public String getOriginRequestURI() {
        return requestInfo.getRequestURI();
    }

    public HttpMethod getHttpMethod() {
        return requestInfo.getHttpMethod();
    }

    public boolean isSameParsedRequestURI(final String uri) {
        return requestInfo.isSameParsedRequestURI(uri);
    }

    public boolean hasHeader(final String header) {
        return httpHeader.hasHeader(header);
    }

    public boolean hasCookie(final String cookie) {
        return this.cookie.hasKey(cookie);
    }

    public String getCookie(final String cookie) {
        return this.cookie.getCookie(cookie);
    }

    public HttpVersion getHttpVersion() {
        return requestInfo.getHttpVersion();
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.requestInfo.isSameHttpMethod(httpMethod);
    }
}
