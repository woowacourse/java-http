package org.apache.catalina.request;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;

public class Request {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public boolean checkQueryParamIsEmpty() {
        return requestLine.checkQueryParamIsEmpty();
    }

    public String getHttpMethod() {
        return requestLine.getHttpMethod().name();
    }

    public String getPathWithoutQuery() {
        return requestLine.getPathWithoutQuery();
    }

    public Map<String, String> getQueryParam() {
        return requestLine.getQueryParam();
    }

    public Map<String, String> getBody() {
        return requestBody.getBody();
    }

    public String getFileType() {
        return requestHeader.getFileType();
    }

    public HttpCookie getCookie() {
        return requestHeader.getCookie();
    }
}
