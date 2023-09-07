package org.apache.coyote.request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

public class Request {

    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(RequestHeader requestHeader, RequestBody requestBody) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public boolean hasQueryString() {
        return requestHeader.hasQueryString();
    }

    public boolean isSameHttpMethod(HttpMethod otherHttpMethod) {
        return requestHeader.isSameHttpMethod(otherHttpMethod);
    }

    public String getPath() {
        return requestHeader.getPath();
    }

    public Protocol getProtocol() {
        return requestHeader.getProtocol();
    }

    public HttpMethod getHttpMethod() {
        return requestHeader.getHttpMethod();
    }

    public String getResourceTypes() {
        return requestHeader.getHeaderBy("Accept");
    }

    public String getQueryStringValue(String key) {
        return requestHeader.getQueryValue(key);
    }

    public boolean isSamePath(String urlPath) {
        return requestHeader.isSamePath(urlPath);
    }

    public Map<String, String> getQueryString() {
        return requestHeader.getQueryString();
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public Map<String, String> getBody() {
        return requestBody.getBody();
    }
}
