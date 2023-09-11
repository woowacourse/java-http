package org.apache.coyote.http11.request;

import org.apache.catalina.Session;

public class HttpRequest {

    private final RequestLine requestLine;

    private final RequestHeader requestHeader;

    private final RequestBody requestBody;

    private Session session;

    public HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
