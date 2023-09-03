package org.apache.coyote.common;

import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private static final Manager manager = new SessionManager();

    private final RequestUri requestUri;
    private final HttpHeaders headers;
    private final String requestBody;
    private final HttpCookie cookie;

    public HttpRequest(RequestUri requestUri, HttpHeaders headers, HttpCookie cookie, String requestBody) {
        this.requestUri = requestUri;
        this.headers = headers;
        this.cookie = cookie;
        this.requestBody = requestBody;
    }

    public String getPath() {
        return requestUri.getHttpPath().getPath();
    }

    public String getQueryString(String key) {
        return requestUri.getHttpPath().getQueryString(key);
    }

    public QueryString getQueryStrings() {
        return requestUri.getHttpPath().getQueryStrings();
    }

    public HttpMethod getHttpMethod() {
        return requestUri.getHttpMethod();
    }

    public HttpSession getSession() {
        return getSession(false);
    }

    public HttpSession getSession(boolean create) {
        HttpSession session = null;
        String jsessionid = cookie.get("JSESSIONID");
        try {
            session = manager.findSession(jsessionid);
        } catch (IOException e) {
            // ignore
        }
        if (session == null) {
            session = new HttpSession(UUID.randomUUID().toString());
            manager.add(session);
        }
        if (create) {
            manager.remove(session);
            session.invalidate();
            session = new HttpSession(UUID.randomUUID().toString());
            manager.add(session);
        }
        return session;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
