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
    private final RequestCookie requestCookie;

    public HttpRequest(SessionManager sessionManager, String httpMethod, String url, double httpVersion, String host,
                       String contentType, String requestBody, RequestCookie requestCookie) {
        this.sessionManager = sessionManager;
        this.httpMethod = httpMethod;
        this.url = url;
        this.httpVersion = httpVersion;
        this.host = host;
        this.contentType = contentType;
        this.contentLength = requestBody == null ? 0 : requestBody.length();
        this.requestBody = requestBody;
        this.requestCookie = requestCookie;
    }

    public HttpSession getSession(boolean create) {
        if (requestCookie == null) {
            return sessionManager.createSession();
        }

        String jSessionId = requestCookie.findByKey(JAVA_SESSION_ID_KEY);
        if (jSessionId != null) {
            return sessionManager.findSession(jSessionId);
        }

        if (create) {
            sessionManager.createSession();
        }

        return null;
    }

    public RequestCookie getCookie() {
        return requestCookie;
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
