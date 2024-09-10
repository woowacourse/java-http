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
    private Map<String, String> headers;
    private HttpCookie cookie;
    private Map<String, String> parameters;
    private String body;

    public HttpRequest() {
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        this.cookie = new HttpCookie();
    }

    public boolean hasNotParameter(String key) {
        return !parameters.containsKey(key);
    }

    public boolean hasNotParameters() {
        return parameters.isEmpty();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getCookie(String key) {
        return cookie.getCookie(key);
    }

    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
}
