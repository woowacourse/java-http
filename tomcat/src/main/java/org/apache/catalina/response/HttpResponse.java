package org.apache.catalina.response;

import org.apache.catalina.Cookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private StatusLine statusLine;
    private final Map<Header, Object> headers = new LinkedHashMap<>();
    private String body;

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

    public void setContentLength(int contentLength) {
        headers.put(Header.CONTENT_LENGTH, contentLength);
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
