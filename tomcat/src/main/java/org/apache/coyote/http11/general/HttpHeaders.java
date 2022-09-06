package org.apache.coyote.http11.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.session.Cookie;

public class HttpHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpHeaders(Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(List<String> headerLines) {
        Map<String, String> result = new HashMap<>();
        for (String headerLine : headerLines) {
            String[] item = headerLine.split(HEADER_DELIMITER);
            String key = item[KEY_INDEX];
            String value = item[VALUE_INDEX];
            result.put(key, value);
        }
        return new HttpHeaders(result);
    }

    public void add(String key, String value) {
        values.put(key, value);
    }

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Non exists key = " + key);
        }
        return values.get(key);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String toHttpResponse() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                .collect(Collectors.joining("\r\n"));
    }

    public void addCookie(Cookie cookie) {
        values.put("Set-Cookie", cookie.headerValue());
    }

    public boolean hasCookie() {
        return values.containsKey("Cookie");
    }

    public Cookie getCookie() {
        return Cookie.from(values.get("Cookie"));
    }
}
