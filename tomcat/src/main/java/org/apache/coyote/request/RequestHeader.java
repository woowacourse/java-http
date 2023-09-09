package org.apache.coyote.request;

import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

public class RequestHeader {

    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String JSESSIONID = "JSESSIONID";

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public RequestHeader(RequestLine requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public String getHeader(String key) {
        return headers.getOrDefault(key, null);
    }

    public int getContentLength() {
        if (has("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }

    public boolean has(String key) {
        return headers.containsKey(key);
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
        if (has("Cookie")) {
            return HttpCookie.from(headers.get("Cookie"));
        }
        return HttpCookie.from("");
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
