package org.apache.coyote.http11.request;

import java.io.IOException;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, RequestBody body) throws IOException {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpCookie getCookie() {
        return new HttpCookie(headers.getCookie());
    }

    public String getURI() {
        return requestLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getMethod();
    }

    public RequestBody getBody() {
        return body;
    }
}
