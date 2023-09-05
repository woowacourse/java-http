package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.session.Cookie;

public class RequestHeader {

    public static final String HEADER_DELIMITER = ": ";

    private final RequestInfo requestInfo;
    private final Map<String, String> headers;
    private final Cookie cookie;

    private RequestHeader(final RequestInfo requestInfo, final Map<String, String> headers, final Cookie cookie) {
        this.requestInfo = requestInfo;
        this.headers = headers;
        this.cookie = cookie;
    }

    public static RequestHeader from(final List<String> requestHeader) {
        final Map<String, String> headers = parseRequestHeaders(requestHeader);
        final RequestInfo requestInfo = new RequestInfo(requestHeader.get(0));
        final Cookie cookie = Cookie.from(headers);
        return new RequestHeader(requestInfo, headers, cookie);
    }

    private static Map<String, String> parseRequestHeaders(final List<String> requestHeader) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < requestHeader.size(); i++) {
            final String[] splitHeader = requestHeader.get(i).split(HEADER_DELIMITER);
            headers.put(splitHeader[0], splitHeader[1]);
        }
        return headers;
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

    public String getHeader(final String header) {
        return headers.get(header);
    }

    public boolean hasHeader(final String header) {
        return headers.containsKey(header);
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
}
