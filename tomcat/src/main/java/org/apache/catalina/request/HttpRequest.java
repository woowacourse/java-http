package org.apache.catalina.request;

import org.apache.catalina.Cookie;

import java.util.Map;

public class HttpRequest {

    private final StartLine startLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(StartLine startLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.startLine = startLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public Map<String, String> getBody() {
        return requestBody.getBody();
    }

    public String getSessionId() {
        String cookie = requestHeader.getCookie();
        Cookie cookies = Cookie.parse(cookie);
        return cookies.getValue("JSESSIONID");
    }
}
