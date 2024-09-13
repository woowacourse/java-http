package org.apache.catalina.connector;

import org.apache.tomcat.http.common.Method;
import org.apache.tomcat.http.common.body.Body;
import org.apache.tomcat.http.exception.NotFoundException;
import org.apache.tomcat.http.request.RequestHeaders;
import org.apache.tomcat.http.request.RequestLine;
import org.apache.tomcat.http.response.Cookie;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Body body;
    private final Cookie cookie;

    public HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final Body body,
                       final Cookie cookie) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.body = body;
        this.cookie = cookie;
    }

    public boolean isSameMethod(final Method method) {
        return requestLine.getMethod() == method;
    }

    public String getCookieContent(final String key) {
        return cookie.get(key);
    }

    public String getBodyContent(final String key) {
        final var content = body.getContent(key);
        if (content.isBlank()) {
            throw new NotFoundException("바디 값 찾을 수 없음");
        }
        return content;
    }

    public String getUriPath() {
        return requestLine.getPath();
    }
}
