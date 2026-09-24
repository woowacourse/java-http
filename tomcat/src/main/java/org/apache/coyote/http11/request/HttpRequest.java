package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;
    private final Cookies cookies;

    public HttpRequest(RequestLine requestLine, Headers headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
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

    public String getBody() {
        return requestBody.getValue();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

}
