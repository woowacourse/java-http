package org.apache.coyote.dto;

import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;

public record HttpRequest(RequestLine requestLine, HttpHeader header) {

    public String method() {
        return requestLine.method();
    }
    public String path() {
        return requestLine.path();
    }

    public String version() {
        return requestLine.version();
    }

    public Map<String, String> queryParams() {
        return requestLine.queryParams();
    }

    public HttpCookie getCookie() {
        final String cookieHeader = header.getHeaders().get("Cookie");
        return new HttpCookie(cookieHeader);
    }

}