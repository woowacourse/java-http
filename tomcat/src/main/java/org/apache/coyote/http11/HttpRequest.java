package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;

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
            System.out.println("======= 쿠키 없음 ======= ");
            return sessionManager.createSession();
        }

        String jSessionId = cookie.findByKey("JSESSIONID");

        if (jSessionId != null) {
            System.out.println("======= 세션 있어서 찾아서 반환함 ======= ");
            return sessionManager.findSession(jSessionId);
        }

        if (create) {
            System.out.println("======= 세션 없어서 만들어서 줌 ======= ");
            sessionManager.createSession();
        }

        System.out.println("======= 세션 필요 없음 ======= ");
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
