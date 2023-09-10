package org.apache.coyote.request;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private final Map<String, Object> headers;

    private RequestHeader(final Map<String, Object> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> requestLines) {
        final Map<String, Object> headers = new LinkedHashMap<>();
        for (String line : requestLines) {
            if (!line.contains(":")) {
                break;
            }
            addHeaders(headers, line);
        }
        return new RequestHeader(headers);
    }

    private static void addHeaders(final Map<String, Object> headers, final String line) {
        final String[] header = line.split(":", 2);
        if (COOKIE.equals(header[0])) {
            final Cookie cookie = Cookie.from(header[1]);
            headers.put(header[0], cookie);
            return;
        }
        headers.put(header[0], header[1]);
    }

    public int getContentLength() {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return 0;
        }
        final String contentLength = headers.get(CONTENT_LENGTH)
                .toString()
                .strip();
        return Integer.parseInt(contentLength);
    }

    public boolean hasJsessionid() {
        if (!headers.containsKey(COOKIE)) {
            return false;
        }
        final Object cookieObject = headers.get(COOKIE);
        if (cookieObject instanceof Cookie) {
            final Cookie cookie = (Cookie) cookieObject;
            return cookie.hasJsessionid();
        }
        return false;
    }

    public String getJsessionid() {
        if (!headers.containsKey(COOKIE)) {
            return "";
        }
        final Object cookieObject = headers.get(COOKIE);
        if (cookieObject instanceof Cookie) {
            final Cookie cookie = (Cookie) cookieObject;
            return cookie.getJsessionid();
        }
        return "";
    }

    @Override
    public String toString() {
        return headers.keySet()
                .stream()
                .map(key -> key + ":" + headers.get(key))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
