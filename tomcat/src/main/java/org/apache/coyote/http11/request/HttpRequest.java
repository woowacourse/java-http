package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.InputReader;

import java.io.IOException;
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(InputReader inputReader) throws IOException {
        this.requestLine = new RequestLine(inputReader.readRequestLine());
        this.headers = new RequestHeaders(inputReader.readHeaders());
        this.body = new RequestBody(inputReader.readBody(headers.getContentLength()));
    }

    public boolean isGetMethod() {
        return requestLine.isGetMethod();
    }

    public boolean isPostMethod() {
        return requestLine.isPostMethod();
    }

    public boolean hasQueryParameter() {
        return requestLine.hasQueryParameter();
    }

    public boolean hasJSessionCookie() {
        return headers.hasJSessionCookie();
    }

    public QueryParameter getQueryParameter() {
        return requestLine.getQueryParameter();
    }

    public Session getSession(boolean flag) {
        if (hasJSessionCookie()) {
            return headers.getSession();
        }
        if (flag) {
            return getSession();
        }
        return null;
    }

    public Session getSession() {
        String uuid = UUID.randomUUID().toString();
        return new Session(uuid);
    }

    public String getJSessionId() {
        return headers.getJSessionId();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getBody() {
        return body.getValue();
    }
}
