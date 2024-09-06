package org.apache.catalina.request;

import org.apache.catalina.Cookie;
import org.apache.catalina.Mapper;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> payload;

    public HttpRequest(String method, String path, Map<String, String> headers, Map<String, String> payload) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.payload = payload;
    }

    public String getRequestMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public String getSessionId() {
        String cookie = headers.get("Cookie");
        Cookie cookies = new Cookie(Mapper.toMap(cookie));
        return cookies.getValue("JSESSIONID");
    }
}
