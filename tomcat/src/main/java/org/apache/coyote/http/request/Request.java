package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;

public class Request {

    private final HttpRequestHeaders headers;
    private final RequestLine requestLine;
    private final HttpRequestBody body;
    private final Parameters parameters;
    private final HttpCookie cookie;

    public Request(
            final HttpRequestHeaders headers,
            final RequestLine requestLine,
            final HttpRequestBody body,
            final Parameters parameters
    ) {
        this(headers, requestLine, body, parameters, HttpCookie.EMPTY);
    }

    public Request(
            final HttpRequestHeaders headers,
            final RequestLine requestLine,
            final HttpRequestBody body,
            final Parameters parameters,
            final HttpCookie cookie
    ) {
        this.headers = headers;
        this.requestLine = requestLine;
        this.body = body;
        this.parameters = parameters;
        this.cookie = cookie;
    }

    public String findHeaderValue(final String headerKey) {
        return headers.findValue(headerKey);
    }

    public String findParameterValue(final String parameterKey) {
        return parameters.findValue(parameterKey);
    }

    public boolean matchesByMethod(final HttpMethod method) {
        return requestLine.matchesByMethod(method);
    }

    public boolean matchesByPathExcludingContextPath(final String targetPath, final String contextPath) {
        return requestLine.matchesByPathExcludingContextPath(targetPath, contextPath);
    }

    public boolean matchesByContextPath(final String contextPath) {
        return requestLine.matchesByContextPath(contextPath);
    }

    public boolean isWelcomePageRequest(final String contextPath) {
        return requestLine.isWelcomePageRequest(contextPath);
    }

    public boolean isStaticResource() {
        return requestLine.isStaticResource();
    }

    public boolean hasQueryParameters() {
        return parameters.size() > 0;
    }

    public HttpVersion version() {
        return requestLine.version();
    }

    public String url() {
        return requestLine.url().url();
    }

    public String resourceName() {
        return requestLine.url().resourceName();
    }

    public HttpSession getSession(final boolean create, final SessionManager sessionManager) {
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
