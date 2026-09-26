package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class HttpHeaders {

    private static final String DELIMITER = ":";
    private static final String COOKIE = "Cookie";

    private final HttpCookie cookie;

    private HttpHeaders(Map<String, String> values) {
        this.cookie = new HttpCookie(values.get(COOKIE));
    }

    static HttpHeaders from(List<String> lines) {
        Map<String, String> values = lines.stream()
                .map(line -> line.split(DELIMITER, 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0].trim(),
                        keyValue -> keyValue[1].trim(),
                        (previous, current) -> current));
        return new HttpHeaders(values);
    }

    String getSessionId() {
        return cookie.getJSessionId();
    }
}
