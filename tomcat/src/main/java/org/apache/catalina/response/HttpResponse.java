package org.apache.catalina.response;

import org.apache.catalina.Cookie;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private StatusLine statusLine;
    private final Map<Header, Object> headers = new HashMap<>();
    private String body;

    public HttpResponse() {
    }

    public void setStatusLine(Status status) {
        this.statusLine = new StatusLine(status);
    }

    public void setLocation(String location) {
        headers.put(Header.LOCATION, location);
    }

    public void setCookie(Cookie cookie) {
        headers.put(Header.SET_COOKIE, cookie);
    }

    public void setContentType(ContentType contentType) {
        headers.put(Header.CONTENT_TYPE, contentType);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<Header, Object> getHeaders() {
        return headers;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public String getBody() {
        return body;
    }
}
