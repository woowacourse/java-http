package org.apache.coyote.http11;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class Request {

    private final RequestHeader requestHeader;
    private final RequestBody requestBody;
    private final RequestParams requestParams;

    public Request(RequestHeader requestHeader, RequestBody requestBody, RequestParams requestParams) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.requestParams = requestParams;
    }

    public HttpMethod getMethod() {
        return requestHeader.method();
    }

    public String getPath() {
        return requestHeader.getPath();
    }

    public String getRequestParam(String key) {
        if (requestParams == null) {
            return "";
        }
        return requestParams.getParams(key);
    }

    public String getContentTypeName() {
        return requestHeader.getContentTypeName();
    }

    public boolean hasJSessionId() {
        return requestHeader.hasJSessionId();
    }

    public HttpCookie getCookie() {
        return requestHeader.cookie();
    }

    public String getJSessionId() {
        return requestHeader.getJSessionId();
    }

    @Override
    public String toString() {
        String method = requestHeader.getMethodName();
        String path = requestHeader.getPath();
        String jSessionId = requestHeader.getJSessionId();
        String contentType = requestHeader.getContentTypeName();
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", requestParams=" + requestParams +
                ", contentType=" + contentType +
                ", jSessionId='" + jSessionId + '\'' +
                '}';
    }

    public Session getSession(boolean create) {
        SessionManager manager = SessionManager.getInstance();
        Session session = manager.findSession(getJSessionId());
        if (session == null && create) {
            session = new Session(UUID.randomUUID().toString());
            manager.add(session);
        }
        return session;
    }
}
