package org.apache.coyote.request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class Request {

    private static final String JSESSIONID = "JSESSIONID";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public boolean isSameHttpMethod(HttpMethod otherHttpMethod) {
        return requestLine.isSameHttpMethod(otherHttpMethod);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getResourceTypes() {
        return requestHeader.getResourceType();
    }

    public String getSessionId() {
        return requestHeader.getCookie().getValue(JSESSIONID);
    }

    public boolean isSamePath(String urlPath) {
        return requestLine.isSamePath(urlPath);
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }

    public Map<String, String> getBody() {
        return requestBody.getBody();
    }
}
