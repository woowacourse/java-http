package org.apache.coyote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private static final String HEADER_DELIMITER = ": ";
    private static final String EMPTY_STRING = "";

    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final HttpCookie cookie;
    private final Map<String, String> values;

    public HttpHeaders(final Map<String, String> values) {
        this(HttpCookie.from(""), values);
    }

    private HttpHeaders(final HttpCookie cookie, final Map<String, String> values) {
        this.cookie = cookie;
        this.values = values;
    }

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();
        HttpCookie cookie = HttpCookie.from("");

        for (final String header : rawHeaders) {
            if (EMPTY_STRING.equals(header)) {
                break;
            }

            final String[] headerKeyValue = header.split(HEADER_DELIMITER);
            if (COOKIE.equals(headerKeyValue[KEY])) {
                cookie = HttpCookie.from(headerKeyValue[VALUE]);
            }

            headers.put(headerKeyValue[KEY], headerKeyValue[VALUE].trim());
        }

        return new HttpHeaders(cookie, headers);
    }

    public void addCookie(final String key, final String value) {
        cookie.add(key, value);
    }

    public void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public String getCookie(final String key) {
        return cookie.get(key);
    }

    public String getAllCookie() {
        return cookie.getAllCookies();
    }

    public String getHeader(final String header) {
        return values.get(header);
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                .collect(Collectors.joining("\r\n"));
    }

    public boolean hasCookie() {
        return !cookie.isEmpty();
    }
}
