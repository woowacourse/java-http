package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;

public class HttpRequest {
    private final String method;
    private final String url;
    private final Map<String, String> header;
    private final HttpCookie cookie;
    private final String body;

    public HttpRequest(String method, String url, Map<String, String> header, HttpCookie cookie, String body) {
        this.method = method;
        this.url = url;
        this.header = header;
        this.cookie = cookie;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getBody() {
        return body;
    }
}
