package org.apache.coyote.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String COOKIE_FIELD_DELIMITER = ": ";

    private static final String COOKIE_FIELD = "Cookie";
    private static final String CONTENT_LENGTH_FIELD = "Content-Length";

    private final Map<String, String> values;

    public RequestHeaders(String[] headers) {
        this.values = Arrays.stream(headers)
                .filter(header -> header.contains(COOKIE_FIELD_DELIMITER))
                .map(header -> header.split(COOKIE_FIELD_DELIMITER, 2))
                .filter(token -> token.length == 2)
                .collect(Collectors.toMap(token -> token[0].trim(), token -> token[1].trim()));
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getCookies() {
        return values.getOrDefault(COOKIE_FIELD, "");
    }

    public int getContentLength() {
        return Integer.parseInt(values.getOrDefault(CONTENT_LENGTH_FIELD, "0"));
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
               "values=" + values +
               '}';
    }
}
