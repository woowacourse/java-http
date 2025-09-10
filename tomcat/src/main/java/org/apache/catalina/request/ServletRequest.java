package org.apache.catalina.request;

import java.util.Objects;
import org.apache.catalina.cookie.HttpCookie;
import org.apache.catalina.cookie.HttpCookieName;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpHeaderName;
import org.apache.coyote.HttpRequest;

public class ServletRequest {

    private static final char PATH_DELIMITER = '?';

    private final String method;
    private final String path;
    private final Parameters parameters;
    private final HttpHeader headers;
    private final HttpCookie cookies;
    private final String body;
    private Session session;

    public ServletRequest(HttpRequest request) {
        this.method = request.getMethod();
        this.headers = new HttpHeader(request.getHeaders());
        this.path = parsePath(request.getUri());
        this.body = request.getBody();
        this.parameters = new Parameters(request.getUri(), body, headers.get(HttpHeaderName.CONTENT_TYPE.getValue()));
        this.cookies = new HttpCookie(headers.get(HttpHeaderName.COOKIE.getValue()));
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
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
                return existingSession;
            }
        }

        if (create) {
            return createNewSession();
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

        return !Objects.equals(getCookie(HttpCookieName.JSESSIONID.getValue()), session.getId());
    }

    private String parsePath(String uri) {
        final int queryIndex = uri.indexOf(PATH_DELIMITER);

        if (queryIndex == -1) {
            return uri;
        }
        return uri.substring(0, queryIndex);
    }

    private Session createNewSession() {
        final Session newSession = new Session();
        SessionManager.getInstance().add(newSession);

        this.session = newSession;

        return newSession;
    }
}
