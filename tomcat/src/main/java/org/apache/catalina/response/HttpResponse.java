package org.apache.catalina.response;

import org.apache.catalina.Cookie;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private Status status;
    private final Map<Header, Object> headers = new HashMap<>();
    private String body;

    public HttpResponse() {
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setLocation(String location) {
        headers.put(Header.LOCATION, location);
    }

    public void setCookie(Cookie cookie) {
        headers.put(Header.SET_COOKIE, cookie);
    }

    public void setContentType(String contentType) {
        headers.put(Header.CONTENT_TYPE, contentType);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<Header, Object> getHeaders() {
        return headers;
    }

    public Status getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
