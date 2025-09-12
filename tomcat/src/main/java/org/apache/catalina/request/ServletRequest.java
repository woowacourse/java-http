package org.apache.catalina.request;

import org.apache.catalina.cookie.HttpCookie;
import org.apache.catalina.cookie.HttpCookieName;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpHeaderName;
import org.apache.coyote.HttpRequest;

public class ServletRequest {

    private final HttpMethod method;
    private final Path path;
    private final Parameters parameters;
    private final HttpHeader headers;
    private final HttpCookie cookies;
    private final String body;
    private Session session;

    public ServletRequest(HttpRequest request) {
        this.method = HttpMethod.of(request.getMethod());
        this.headers = new HttpHeader(request.getHeaders());
        this.path = new Path(request.getUri());
        this.body = request.getBody();
        this.parameters = new Parameters(request.getUri(), body, headers.get(HttpHeaderName.CONTENT_TYPE.getValue()));
        this.cookies = new HttpCookie(headers.get(HttpHeaderName.COOKIE.getValue()));
    }

    public Path getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getParameter(String name) {
        return parameters.getParameter(name);
    }

    public Session getSession(boolean create) {

        if (session != null) {
            return session;
        }

        final String sessionId = getCookie(HttpCookieName.JSESSIONID.getValue());
        if (sessionId != null) {
            final Session existingSession = SessionManager.getInstance().findSession(sessionId);
            if (existingSession != null) {
                existingSession.activate();
                return existingSession;
            }
        }

        if (create) {
            this.session = SessionManager.getInstance().create();
            return session;
        }
        return null;
    }

    public String getCookie(String name) {
        return cookies.getCookie(name);
    }

    public boolean isSessionCreated() {
        if (session == null) {
            return false;
        }

        return session.isNew();
    }
}
