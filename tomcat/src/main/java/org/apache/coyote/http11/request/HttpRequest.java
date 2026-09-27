package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestBody requestBody;
    private final Cookies cookies;

    public HttpRequest(
            final RequestLine requestLine,
            final Headers headers,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.requestBody = requestBody;
        this.cookies = new Cookies(headers.cookie());
    }

    public String getSessionId() {
        return cookies.getSessionId();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getQueryParameter(final String name) {
        return requestLine.getParameter(name);
    }

    public String getBodyParameter(final String name) {
        return requestBody.getParameter(name);
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

}
