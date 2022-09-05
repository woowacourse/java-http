package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String DELIMITER = ":";
    private static final int KEY = 0;
    private static final int VALUE = 1;

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
            values.put(parseKey(header[KEY]), parseValue(header[VALUE]));
        }
        return values;
    }

    private static String parseKey(final String key) {
        return key.replace(DELIMITER, "");
    }

    private static String parseValue(final String value) {
        return value.strip();
    }

    public String getValue(final String key) {
        return headers.get(key);
    }
}
