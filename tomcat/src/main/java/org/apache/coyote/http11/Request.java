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
        return requestHeader.getMethod();
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

    public HttpCookie getCookie() {
        return requestHeader.getHttpCookie();
    }

    public String getJSessionId() {
        return requestHeader.getJSessionId();
    }

    @Override
    public String toString() {
        HttpMethod method = requestHeader.getMethod();
        String path = requestHeader.getPath();
        String jSessionId = requestHeader.getJSessionId();
        ContentType contentType = requestHeader.getContentType();
        return "Request{" +
                "method='" + method.getName() + '\'' +
                ", path='" + path + '\'' +
                ", requestParams=" + requestParams +
                ", contentType=" + contentType.getName() +
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

    public ContentType getContentType() {
        return requestHeader.getContentType();
    }
}
