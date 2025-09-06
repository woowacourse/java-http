package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Cookie;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private final SessionManager sessionManager;

    private final String httpMethod;
    private final String url;
    private final double httpVersion;
    private final String host;
    private final String contentType;
    private final int contentLength;
    private final String requestBody;
    private final Cookie cookie;

    public HttpRequest(SessionManager sessionManager, String httpMethod, String url, double httpVersion, String host,
                       String contentType, String requestBody, Cookie cookie) {
        this.sessionManager = sessionManager;
        this.httpMethod = httpMethod;
        this.url = url;
        this.httpVersion = httpVersion;
        this.host = host;
        this.contentType = contentType;
        this.contentLength = requestBody == null ? 0 : requestBody.length();
        this.requestBody = requestBody;
        this.cookie = cookie;
    }

    public HttpSession getSession(boolean create) {
        if (cookie == null) {
            return sessionManager.createSession();
        }

        String jSessionId = cookie.findByKey("JSESSIONID");
        if (jSessionId != null) {
            return sessionManager.findSession(jSessionId);
        }

        if (create) {
            sessionManager.createSession();
        }

        return null;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public double getHttpVersion() {
        return httpVersion;
    }
}
