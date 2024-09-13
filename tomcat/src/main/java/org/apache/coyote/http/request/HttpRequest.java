package org.apache.coyote.http.request;

import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestParameters parameters;
    private final Cookie cookie;
    private final RequestBody body;

    public HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestParameters parameters,
                       Cookie cookie, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookie = cookie;
        this.body = body;
        this.parameters = parameters;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public RequestHeaders getHeader() {
        return headers;
    }

    public String getParameter(String key) {
        return parameters.getValue(key);
    }

    public Cookie getCookie() {
        return cookie;
    }

    public RequestBody getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
               "requestLine=" + requestLine +
               ", headers=" + headers +
               ", parameters=" + parameters +
               ", cookie=" + cookie +
               ", body='" + body + '\'' +
               '}';
    }
}
