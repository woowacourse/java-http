package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpCookies;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;

public class HttpRequest {

    public static final String JSESSION_COOKIE_NAME = "JSESSIONID";
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, HttpMessageBody httpMessageBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public boolean isGet() {
        return requestLine.isMethod(Method.GET);
    }

    public boolean isPost() {
        return requestLine.isMethod(Method.POST);
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
}
