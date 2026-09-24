package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final FormContents formContents;
    private final Cookies cookies;
    private String sessionId;

    public HttpRequest(
            RequestLine requestLine,
            HttpHeaders headers,
            FormContents formContents,
            Cookies cookies
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.formContents = formContents;
        this.cookies = cookies;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        return new Http11RequestParser().parse(inputStream);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public boolean isGet() {
        return getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return getMethod() == HttpMethod.POST;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getResourcePath() {
        return requestLine.getResourcePath();
    }

    public Optional<String> getParameter(String name) {
        Optional<String> query = requestLine.findParameter(name);
        if (query.isPresent()) {
            return query;
        }

        return formContents.find(name);
    }

    public Optional<Cookie> getCookie(String name) {
        return cookies.find(name);
    }

    public Optional<String> getHeader(String name) {
        return headers.find(name);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
