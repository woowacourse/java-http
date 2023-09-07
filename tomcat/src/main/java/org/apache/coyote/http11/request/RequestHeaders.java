package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.HttpCookie;

public class RequestHeaders {

    private final Map<String, String> headers;
    private final HttpCookie httpCookie;

    private RequestHeaders(Map<String, String> headers, HttpCookie httpCookie) {
        this.headers = headers;
        this.httpCookie = httpCookie;
    }

    public static RequestHeaders from(Map<String, String> headers) {
        String cookie = headers.remove("Cookie");

        return new RequestHeaders(headers, HttpCookie.from(cookie));
    }

    public String get(String key) {
        return headers.get(key);
    }

    public String contentLength() {
        return headers.getOrDefault("Content-Length", "0");
    }

    public String findCookie(String key) {
        return httpCookie.find(key);
    }

    public String findJSessionId() {
        return httpCookie.findJSessionId();
    }

}
