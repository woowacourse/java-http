package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpCookies;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMessageBody;
import org.apache.coyote.http11.common.HttpProtocol;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.RequestLine;

public class HttpServletRequest {

    public static final String JSESSION_COOKIE_NAME = "JSESSIONID";
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpServletRequest(RequestLine requestLine, HttpHeaders httpHeaders, HttpMessageBody httpMessageBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public boolean methodEquals(Method method) {
        return requestLine.isMethod(method);
    }

    public boolean isGet() {
        return requestLine.isMethod(Method.GET);
    }

    public boolean isPost() {
        return requestLine.isMethod(Method.POST);
    }

    public boolean protocolEquals(HttpProtocol httpProtocol) {
        return requestLine.isHttpProtocol(httpProtocol);
    }

    public boolean isUriHome() {
        return requestLine.isUriHome();
    }

    public String getUriPath() {
        return requestLine.getUriPath();
    }

    public String getFormData(String name) {
        return httpMessageBody.getFormData(name);
    }

    public String getSessionId() {
        HttpCookies cookies = HttpCookies.from(httpHeaders);
        return cookies.getCookieValue(JSESSION_COOKIE_NAME);
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public HttpProtocol getProtocol() {
        return requestLine.getProtocol();
    }
}
