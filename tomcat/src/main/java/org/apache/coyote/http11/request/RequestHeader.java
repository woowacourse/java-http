package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeader {

    private final Map<String, String> value;
    private final Cookie cookie;

    public RequestHeader(Map<String, String> value, Cookie cookie) {
        this.value = value;
        this.cookie = cookie;
    }

    public static RequestHeader from(Map<String, String> header) {
        Cookie cookie = Cookie.empty();
        if (header.containsKey("Cookie")) {
            cookie = Cookie.from(header.remove("Cookie"));
        }
        return new RequestHeader(header, cookie);
    }
}
