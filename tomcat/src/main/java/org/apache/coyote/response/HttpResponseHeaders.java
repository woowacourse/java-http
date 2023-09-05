package org.apache.coyote.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeaders {

    private static final String DELIMITER = ": ";
    private static final String LINE_DELIMITER = " " + "\r\n";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponseHeaders() {
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addCookie(final String name, final String value) {
        headers.put("Set-Cookie", String.join(COOKIE_DELIMITER, name, value));
    }

    @Override
    public String toString() {
        return headers.entrySet()
                .stream()
                .map(entry -> String.join(DELIMITER, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(LINE_DELIMITER, "", LINE_DELIMITER));
    }
}
