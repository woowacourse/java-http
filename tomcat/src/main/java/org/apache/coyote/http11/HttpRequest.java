package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private final Method method;
    private final String path;
    private final HttpVersion httpVersion;
    private final ContentType contentType;
    private final int contentLength;
    private final List<HttpCookie> cookies;
    private final Map<String, String> queryParameter;
    private final Map<String, String> body;

    public HttpRequest(Method method,
                       String path,
                       HttpVersion httpVersion,
                       ContentType contentType,
                       int contentLength, List<HttpCookie> httpCookie,
                       Map<String, String> queryParameter,
                       Map<String, String> body) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.cookies = httpCookie;
        this.queryParameter = queryParameter;
        this.body = body;
    }

    public Session getSession(boolean create) {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = getCookieValue("JSESSIONID");

        if (sessionId != null) {
            Session session = sessionManager.getAttribute(sessionId);
            if (session != null) return session;
        }

        if (create) {
            String newSessionId = generateSessionId();
            Session newSession = new Session(newSessionId);
            sessionManager.setSession(newSessionId, newSession);
            return newSession;
        }

        return null;
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getQueryParameterValue(String key) {
        String value = queryParameter.get(key);
        if (value == null || value.isBlank()) {
            return "";
        }
        return value;
    }

    public boolean hasCookie(String key) {
        HttpCookie httpCookie = cookies.stream()
                .filter(cookie -> cookie.isKey(key))
                .findFirst()
                .orElse(null);
        return httpCookie != null;
    }

    public String getCookieValue(String key) {
        return cookies.stream()
                .filter(cookie -> cookie.isKey(key))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", httpVersion=" + httpVersion +
                ", contentType=" + contentType +
                ", contentLength=" + contentLength +
                ", queryParameter=" + queryParameter +
                ", body=" + body +
                '}';
    }
}
