package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.util.HttpHeaderParser;

public class HttpHeaders {

    public static final String COOKIE = "Cookie";
    public static final String RESPONSE_COOKIE = "Set-Cookie";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";

    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public static HttpHeaders of(final List<String[]> headers) {
        return HttpHeaderParser.parse(headers);
    }

    public void addContentType(final String value) {
        headers.put(CONTENT_TYPE, value);
    }

    public void addContentLength(final int length) {
        headers.put(CONTENT_LENGTH, String.valueOf(length));
    }

    public void addLocation(final String url) {
        headers.put(LOCATION, url);
    }

    public void addCookie(final String cookie) {
        headers.put(RESPONSE_COOKIE, cookie);
    }

    public HttpCookie getCookies() {
        String cookies = headers.get(COOKIE);
        if (cookies == null) {
            return new HttpCookie();
        }
        return new HttpCookie(cookies);
    }

    public String getValue(final String key) {
        return headers.get(key);
    }

    public String getResponse() {
        return headers.keySet().stream()
                .map(key -> key + ": " + headers.get(key) + " \r\n")
                .collect(Collectors.joining());
    }
}
