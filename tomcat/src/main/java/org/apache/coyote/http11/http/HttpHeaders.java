package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String COOKIE = "Cookie";
    public static final String RESPONSE_COOKIE = "Set-Cookie";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";

    private static final String DELIMITER = ":";
    private static final int KEY = 0;

    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public static HttpHeaders of(final List<String[]> headers) {
        return new HttpHeaders(toMap(headers));
    }

    public static Map<String, String> toMap(final List<String[]> headers) {
        Map<String, String> values = new HashMap<>();
        for (String[] header : headers) {
            values.put(parseKey(header[KEY]), parseValue(header));
        }
        return values;
    }

    private static String parseKey(final String key) {
        return key.replace(DELIMITER, "");
    }

    private static String parseValue(final String[] value) {
        return String.join("", getHeaderValues(value));
    }

    private static String[] getHeaderValues(final String[] value) {
        String[] result = new String[value.length - 1];
        System.arraycopy(value, 1, result, 0, value.length - 1);
        return result;
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
        return new HttpCookie(cookies.split(";"));
    }

    public String getValue(final String key) {
        return headers.get(key);
    }

    public String getAllToString() {
        return headers.keySet().stream()
                .map(key -> key + ": " + headers.get(key) + " \r\n")
                .collect(Collectors.joining());
    }
}
