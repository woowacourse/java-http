package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {
    private final Map<String, Object> headers;
    private final Cookie cookie;

    public RequestHeaders(final Map<String, Object> headers, final Cookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static RequestHeaders from(final BufferedReader br) {
        final Map<String, Object> headers = br.lines()
                .takeWhile(line -> !line.equals(""))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
        final String cookieValue = (String) headers.get("Cookie");
        headers.remove("Cookie");
        return new RequestHeaders(headers, Cookie.from(cookieValue));
    }

    boolean hasContentType() {
        return headers.containsKey("Content-Type");
    }

    public Object get(final String headerKey) {
        return headers.get(headerKey);
    }

    public String getCookieValue(final String cookieKey) {
        return cookie.get(cookieKey);
    }
}
