package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;
    private final HttpCookies httpCookies;

    private HttpHeaders(Map<String, String> headers, HttpCookies httpCookies) {
        this.headers = headers;
        this.httpCookies = httpCookies;
    }

    public static HttpHeaders from(Map<String, String> headers) {
        String cookie = headers.get("Cookie");
        if (cookie == null) {
            return new HttpHeaders(headers, null);
        }
        return new HttpHeaders(headers, HttpCookies.from(cookie));
    }

    public String getCookieBy(String name) {
        return httpCookies.get(name);
    }

    public boolean isCookieExistBy(String name) {
        return httpCookies != null && httpCookies.isExist(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
