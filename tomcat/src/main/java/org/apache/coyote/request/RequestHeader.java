package org.apache.coyote.request;

import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

public class RequestHeader {

    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE = "Cookie";
    private static final String ACCEPT = "Accept";

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public RequestHeader(RequestLine requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public boolean hasRequestBody() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public Session getSession(boolean isCreate) {
        String sessionId = getCookie().getValue(JSESSIONID);
        if (sessionId == null && isCreate) {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        }
        if (sessionId == null) {
            return null;
        }
        return sessionManager.findSession(sessionId);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.getOrDefault(COOKIE, ""));
    }

    public String getResourceType() {
        return headers.get(ACCEPT);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public boolean isSamePath(String otherPath) {
        return requestLine.isSamePath(otherPath);
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }

    public boolean isSameHttpMethod(HttpMethod otherHttpMethod) {
        return requestLine.isSameHttpMethod(otherHttpMethod);
    }
}
