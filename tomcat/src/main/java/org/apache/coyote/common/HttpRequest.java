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
        HttpSession session = findSession();
        if (session != null) {
            return session;
        }
        return createSession();
    }

    private HttpSession findSession() {
        String jsessionid = cookie.get("JSESSIONID");
        try {
            return manager.findSession(jsessionid);
        } catch (IOException e) {
            // ignore
            throw new IllegalArgumentException(e);
        }
    }

    private HttpSession createSession() {
        HttpSession session = new HttpSession(UUID.randomUUID().toString());
        manager.add(session);
        return session;
    }

    public HttpSession getFreshSession() {
        HttpSession session = findSession();
        if (session != null) {
            session.invalidate();
            manager.remove(session);
        }
        return createSession();
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
