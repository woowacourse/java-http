package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String EMPTY = "";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String HEADER_VALUE_DELIMITER = ": ";
    private static final int HEADER_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, Object> headers;
    private final Cookie cookie;

    public RequestHeaders(final Map<String, Object> headers, final Cookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static RequestHeaders from(final BufferedReader br) {
        final Map<String, Object> headers = br.lines()
                .takeWhile(line -> !line.equals(EMPTY))
                .map(line -> line.split(HEADER_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[HEADER_INDEX], line -> line[VALUE_INDEX]));
        final String cookieValue = (String) headers.get(COOKIE);
        headers.remove(COOKIE);
        return new RequestHeaders(headers, Cookie.from(cookieValue));
    }

    boolean hasContentType() {
        return headers.containsKey(CONTENT_TYPE);
    }

    public Object get(final String headerKey) {
        return headers.get(headerKey);
    }

    public String getCookieValue(final String cookieKey) {
        return cookie.get(cookieKey);
    }
}
