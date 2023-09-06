package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpSession;

public class HttpRequest {

    private static final boolean SESSION_CREATED = true;

    private final Request request;

    public HttpRequest(final Request request) {
        this.request = request;
    }

    public String getHeader(final String name) {
        return request.findHeaderValue(name);
    }

    public HttpSession getSession(final boolean create) {
        return request.getSession(create);
    }

    public HttpSession getSession() {
        return request.getSession(SESSION_CREATED);
    }

    public String getParameter(final String name) {
        return request.findParameterValue(name);
    }
}
