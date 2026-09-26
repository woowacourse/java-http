package org.apache.coyote.http11.request;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {

    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;
    private final HttpRequestParams httpRequestParams;

    public HttpRequest(HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody,
                       HttpRequestParams httpRequestParams) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
        this.httpRequestParams = httpRequestParams;
    }

    public HttpMethod getMethod() {
        return httpRequestHeader.getMethod();
    }

    public String getPath() {
        return httpRequestHeader.getPath();
    }

    public String getRequestParam(String key) {
        if (httpRequestParams == null) {
            return "";
        }
        return httpRequestParams.getParams(key);
    }

    public HttpCookie getCookie() {
        return httpRequestHeader.getHttpCookie();
    }

    public String getJSessionId() {
        return httpRequestHeader.getJSessionId();
    }

    @Override
    public String toString() {
        HttpMethod method = httpRequestHeader.getMethod();
        String path = httpRequestHeader.getPath();
        String jSessionId = httpRequestHeader.getJSessionId();
        ContentType contentType = httpRequestHeader.getContentType();
        return "Request{" +
                "method='" + method.getName() + '\'' +
                ", path='" + path + '\'' +
                ", requestParams=" + httpRequestParams +
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
        return httpRequestHeader.getContentType();
    }

    public String getProtocolVersion() {
        return httpRequestHeader.getProtocolVersion();
    }
}
