package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;

public class Request {

    private final HttpRequestHeaders headers;
    private final HttpMethod method;
    private final HttpVersion version;
    private final Url url;
    private final HttpRequestBody body;
    private final Parameters parameters;
    private final HttpCookie cookie;
    private SessionManager sessionManager;

    public Request(
            final HttpRequestHeaders headers,
            final HttpMethod method,
            final HttpVersion version,
            final Url url,
            final HttpRequestBody body,
            final Parameters parameters
    ) {
        this(headers, method, version, url, body, parameters, HttpCookie.EMPTY);
    }

    public Request(
            final HttpRequestHeaders headers,
            final HttpMethod method,
            final HttpVersion version,
            final Url url,
            final HttpRequestBody body,
            final Parameters parameters,
            final HttpCookie cookie
    ) {
        this.headers = headers;
        this.method = method;
        this.version = version;
        this.url = url;
        this.body = body;
        this.parameters = parameters;
        this.cookie = cookie;
    }

    public void initSessionManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String findHeaderValue(final String headerKey) {
        return headers.findValue(headerKey);
    }

    public String findParameterValue(final String parameterKey) {
        return parameters.findValue(parameterKey);
    }

    public boolean matchesByMethod(final HttpMethod method) {
        return this.method.matches(method);
    }

    public boolean matchesByPathExcludingRootContextPath(final String targetPath, final String contextPath) {
        return url.matchesByPathExcludingRootContextPath(targetPath, contextPath);
    }

    public boolean matchesByRootContextPath(final String contextPath) {
        return url.startsWithRootContextPath(contextPath);
    }

    public boolean isWelcomePageRequest(final String contextPath) {
        return url.isWelcomePageUrl(contextPath);
    }

    public boolean isStaticResource() {
        return url.isStaticResource();
    }

    public boolean hasQueryParameters() {
        return parameters.size() > 0;
    }

    public HttpVersion version() {
        return version;
    }

    public String url() {
        return url.url();
    }

    public String resourceName() {
        return url.resourceName();
    }

    public HttpSession getSession(final boolean create) {
        final String sessionId = cookie.findValue(HttpCookie.SESSION_ID_KEY);

        if (create && sessionId == null) {
            final HttpSession httpSession = new HttpSession();
            sessionManager.add(httpSession);

            return httpSession;
        }

        if (sessionId == null) {
            return null;
        }

        return sessionManager.findSession(sessionId);
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
