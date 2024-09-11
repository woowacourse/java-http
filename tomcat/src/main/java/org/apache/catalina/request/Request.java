package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.auth.HttpCookie;

public class Request {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private Map<String, String> body = new HashMap<>();

    public Request(String requestLine, Map<String, String> headers) {
        this.requestLine = new RequestLine(requestLine);
        this.requestHeader = new RequestHeader(headers);
    }

    public boolean checkQueryParamIsEmpty() {
        return requestLine.checkQueryParamIsEmpty();
    }

    public void setBody(Map<String, String> body) {
        this.body = new HashMap<>(body);
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
        return body;
    }

    public String getFileType() {
        return requestHeader.getFileType();
    }

    public int getContentLength() {
        return requestHeader.getContentLength();
    }

    public HttpCookie getCookie() {
        return requestHeader.getCookie();
    }
}
