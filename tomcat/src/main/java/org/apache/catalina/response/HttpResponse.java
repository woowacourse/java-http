package org.apache.catalina.response;

import org.apache.catalina.Cookie;

public class HttpResponse {

    private final int statusCode;
    private final Cookie cookie;
    private final String resource;

    public HttpResponse(int statusCode) {
        this.statusCode = statusCode;
        this.resource = null;
        this.cookie = null;
    }

    public HttpResponse(int statusCode, String resource) {
        this.statusCode = statusCode;
        this.resource = resource;
        this.cookie = null;
    }

    public HttpResponse(int statusCode, String resource, Cookie cookie) {
        this.statusCode = statusCode;
        this.resource = resource;
        this.cookie = cookie;
    }

    public String getResource() {
        return resource;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Cookie getCookie() {
        return cookie;
    }
}
