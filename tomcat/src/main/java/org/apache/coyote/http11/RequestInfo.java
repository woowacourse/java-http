package org.apache.coyote.http11;

import org.apache.coyote.http11.session.Cookie;

public class RequestInfo {

    public static final String QUERY_STRING_CONDITION = "?";
    public static final String QUERY_STRING_DELIMITER = "\\?";

    private final HttpMethod httpMethod;
    private final Cookie cookie;
    private final String requestURI;

    public RequestInfo(final String request, final String cookie) {
        final String[] splitRequest = request.split(" ");
        this.httpMethod = HttpMethod.findHttpMethod(splitRequest[0]);
        this.cookie = Cookie.from(cookie);
        this.requestURI = splitRequest[1];
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

    public boolean hasCookie(final String cookie) {
        return this.cookie.hasKey(cookie);
    }

    public String getCookie(final String cookie) {
        return this.cookie.getCookie(cookie);
    }
}
