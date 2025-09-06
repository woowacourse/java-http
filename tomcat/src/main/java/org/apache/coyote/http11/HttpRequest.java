package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.RequestCookie;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    public static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    private final SessionManager sessionManager;
    private final String httpMethod;
    private final String url;
    private final double httpVersion;
    private final String host;
    private final String contentType;
    private final int contentLength;
    private final String requestBody;
    private final RequestCookie cookie;

    public HttpRequest(SessionManager sessionManager, String httpMethod, String url, double httpVersion, String host,
                       String contentType, String requestBody, RequestCookie cookie) {
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
        String jSessionId = null;
        if (cookie != null) {
            jSessionId = cookie.findByKey(JAVA_SESSION_ID_KEY);
        }

        if (jSessionId != null) {
            HttpSession existingSession = sessionManager.findSession(jSessionId);
            if (existingSession != null) {
                return existingSession;
            }
        }

        if (create) {
            return sessionManager.createSession();
        }

        return null;
    }

    public RequestCookie getCookie() {
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
