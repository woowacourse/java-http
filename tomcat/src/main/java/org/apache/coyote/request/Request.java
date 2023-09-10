package org.apache.coyote.request;

import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpMethod;

public class Request {

    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String JSESSIONID = "JSESSIONID";

    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(RequestHeader requestHeader, RequestBody requestBody) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public boolean isSameHttpMethod(HttpMethod otherHttpMethod) {
        return requestHeader.isSameHttpMethod(otherHttpMethod);
    }

    public HttpMethod getHttpMethod() {
        return requestHeader.getHttpMethod();
    }

    public String getPath() {
        return requestHeader.getPath();
    }

    public String getResourceTypes() {
        return requestHeader.getResourceType();
    }

    public Session getSession(boolean isCreate) {
        String sessionId = requestHeader.getCookie().getValue(JSESSIONID);
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

    public boolean isSamePath(String urlPath) {
        return requestHeader.isSamePath(urlPath);
    }

    public Map<String, String> getQueryString() {
        return requestHeader.getQueryString();
    }

    public Map<String, String> getBody() {
        return requestBody.getBody();
    }
}
