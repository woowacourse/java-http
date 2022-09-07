package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeader {

    private final Map<String, String> value;
    private final HttpCookie httpCookie;

    public RequestHeader(Map<String, String> value, HttpCookie httpCookie) {
        this.value = value;
        this.httpCookie = httpCookie;
    }

    public static RequestHeader from(Map<String, String> header) {
        HttpCookie httpCookie = HttpCookie.empty();
        if (header.containsKey("Cookie")) {
            httpCookie = HttpCookie.from(header.remove("Cookie"));
        }
        return new RequestHeader(header, httpCookie);
    }

    public boolean hasCookie() {
        return httpCookie.exists();
    }

    public String getCookieValue(String key) {
        return httpCookie.getValue(key);
    }
}
