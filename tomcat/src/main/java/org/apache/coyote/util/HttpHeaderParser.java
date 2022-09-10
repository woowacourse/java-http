package org.apache.coyote.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.http.HttpHeaders;

public class HttpHeaderParser {

    private static final String DELIMITER = ":";
    private static final int KEY = 0;

    public static HttpHeaders parse(final List<String[]> values) {
        return new HttpHeaders(toMap(values));
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
}
