package org.apache.coyote.http11.request;

import java.util.Optional;

import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;
    private final RequestCookie cookies;

    public HttpRequest(final RequestLine requestLine, final RequestHeader header, final RequestBody body,
                       final RequestCookie cookies) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
        this.cookies = cookies;
    }

    public RequestMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean existSession() {
        return Optional.ofNullable(cookies.getSessionId())
                .map(SessionManager::findSession)
                .isPresent();
    }

    public Params getParamsFromBody() {
        return body.getParams();
    }
}
