package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.session.HttpSession;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private static final Manager SESSION_MANAGER = SessionManager.INSTANCE;
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(
            RequestLine requestLine,
            RequestHeaders requestHeaders,
            RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public boolean isSameHttpMethod(HttpMethod httpMethod) {
        return requestLine.isSameHttpMethod(httpMethod);
    }

    public boolean isStaticResource() {
        return requestLine.isStaticResource();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpSession getSession(boolean create) {
        if (findSession() != null) {
            return findSession();
        }
        if (create) {
            return new HttpSession(UUID.randomUUID().toString());
        }
        return null;
    }

    public void addSession(HttpSession httpSession) {
        SESSION_MANAGER.add(httpSession);
    }

    private HttpSession findSession() {
        try {
            String jSessionId = requestHeaders.findJSessionId();

            return SESSION_MANAGER.findSession(jSessionId);
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

}
