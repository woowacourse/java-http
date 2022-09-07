package org.apache.coyote.http11.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.constant.HttpMethod;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;

    private final HttpStartLine startLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(final HttpStartLine startLine, final HttpHeaders headers, final HttpBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final List<String> rawHttpRequest) {
        final List<String> request = new ArrayList<>(rawHttpRequest);

        final String startLine = request.remove(START_LINE_INDEX);
        final HttpStartLine httpStartLine = HttpStartLine.from(startLine);
        final HttpHeaders headers = HttpHeaders.from(request);

        return new HttpRequest(httpStartLine, headers, HttpBody.from(null));
    }

    public String getPath() {
        return startLine.getPath();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public boolean isResource() {
        return startLine.isResource();
    }

    public String getParam(final String param) {
        return startLine.getParam(param);
    }

    public String getBodyParam(final String param) {
        return body.getParam(param);
    }

    public String getCookie(final String key) {
        return headers.getCookie(key);
    }

    public Session getSession(final boolean create) {
        final SessionManager sessionManager = new SessionManager();

        if (create) {
            final Session session = new Session(String.valueOf(UUID.randomUUID()));
            sessionManager.add(session);
            return session;
        }
        final String sessionId = getCookie("JSESSIONID");
        return sessionManager.findSession(sessionId);
    }
}
