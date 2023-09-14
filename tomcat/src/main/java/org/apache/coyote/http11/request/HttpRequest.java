package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.header.Cookies;
import org.apache.coyote.http11.request.header.Headers;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.request.header.RequestLine;

import java.nio.file.Path;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Parameters parameters;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final Parameters parameters) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
    }

    public boolean isPost() {
        return requestLine.isMethodEquals(Method.POST);
    }

    public boolean isGet() {
        return requestLine.isMethodEquals(Method.GET);
    }

    public Path getPath() {
        return requestLine.getPath();
    }

    public String getContentType() {
        return requestLine.getContentType();
    }

    public Parameters getParameters() {
        return parameters;
    }

    public String getSession() {
        final Cookies cookies = headers.getCookies();
        return cookies.getJavaSessionId();
    }
}
