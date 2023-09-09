package org.apache.coyote.request;

import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpMethod;

public class Request {

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
        return requestHeader.getHeader("Accept") + ";charset=utf-8";
    }

    public Session getSession(boolean isCreate) {
        return requestHeader.getSession(isCreate);
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
