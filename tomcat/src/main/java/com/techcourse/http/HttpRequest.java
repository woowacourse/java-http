package com.techcourse.http;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRequest {

    private String method;
    private String uri;
    private String path;
    private Map<String, String> parameters;
    private HttpHeaders headers;
    private String body;

    public HttpRequest() {
        this.parameters = new HashMap<>();
        this.headers = new HttpHeaders();
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    public boolean hasNotParameters() {
        return parameters.isEmpty();
    }

    public int getContentLength() {
        String contentLength = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public String getCookie(String key) {
        return headers.getCookie(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }

    public void setHeader(String key, String value) {
        headers.set(key, value);
    }

    public void setCookie(String key, String value) {
        headers.setCookie(key, value);
    }

    public boolean hasContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.equals(headers.get("Content-Type"));
    }
}
