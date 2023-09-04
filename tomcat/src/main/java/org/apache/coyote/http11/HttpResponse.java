package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_PROTOCOL = "HTTP/1.1";

    private final HttpHeaders headers;
    private final Map<String, HttpCookie> cookies = new HashMap<>();
    private HttpStatus httpStatus;
    private String body;

    public HttpResponse() {
        this.headers = new HttpHeaders();
    }

    public String getProtocol() {
        return DEFAULT_PROTOCOL;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void addHeader(String headerName, String value) {
        this.headers.put(headerName, value);
    }

    public void addCookie(HttpCookie cookie) {
        this.cookies.put(cookie.getName(), cookie);
    }

    public HttpCookie getCookie(String name) {
        return this.cookies.get(name);
    }

    public Map<String, HttpCookie> getCookies() {
        return this.cookies;
    }
}
