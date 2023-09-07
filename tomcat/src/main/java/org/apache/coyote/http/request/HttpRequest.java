package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.util.HttpMethod;

public class HttpRequest {

    private static final boolean SESSION_CREATED = true;

    private final Request request;
    private final String contextPath;
    private final SessionManager sessionManager;

    public HttpRequest(final Request request, final String contextPath, final SessionManager sessionManager) {
        this.request = request;
        this.contextPath = contextPath;
        this.sessionManager = sessionManager;
    }

    public boolean matchesByMethod(final HttpMethod method) {
        return request.matchesByMethod(method);
    }

    public boolean matchesByPath(final String path) {
        return request.matchesByPathExcludingContextPath(path, contextPath);
    }

    public boolean isBusinessLogic(final String contextPath) {
        return !(request.isStaticResource() || request.isWelcomePageRequest(contextPath));
    }

    public Request request() {
        return request;
    }

    public String getHeader(final String name) {
        return request.findHeaderValue(name);
    }

    public HttpSession getSession() {
        return getSession(SESSION_CREATED);
    }

    public HttpSession getSession(final boolean create) {
        return request.getSession(create, sessionManager);
    }

    public String getParameter(final String name) {
        return request.findParameterValue(name);
    }
}
