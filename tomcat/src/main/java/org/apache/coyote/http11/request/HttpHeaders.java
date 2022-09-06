package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    public static final String COOKIE = "Cookie";

    private static final String DELIMITER = ":";
    private static final int KEY = 0;

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = Map.copyOf(headers);
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

    public String getValue(final String key) {
        return headers.get(key);
    }
}
