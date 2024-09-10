package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Header {

    private static final String PAIR_DELIMITER = ":";
    private static final String JSESSION_ID_KEY = "JSESSIONID";

    private final Map<String, String> header = new HashMap<>();
    private final Cookie cookie;

    public static Header empty() {
        return new Header(Collections.emptyList());
    }

    public Header(List<String> header) {
        parseHeader(header);

        this.cookie = new Cookie(this.get(HttpHeaderKey.COOKIE).orElse(""));

    }

    private void parseHeader(List<String> header) {
        for (String pair : header) {
            if (pair.contains(PAIR_DELIMITER)) {
                String[] split = pair.split(PAIR_DELIMITER);
                putIfValidPair(split);
            }
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            return;
        }
        String key = keyValuePair[0].trim();
        String value = keyValuePair[1].trim();

        append(key, value);
    }

    public void appendJSessionId(String id) {
        append(HttpHeaderKey.SET_COOKIE, JSESSION_ID_KEY + " = " + id);
    }

    public void append(HttpHeaderKey key, String value) {
        append(key.getName(), value);
    }

    public void append(String key, String value) {
        header.put(key, value);
    }

    public Optional<String> get(HttpHeaderKey key) {
        return get(key.getName());
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(header.get(key));
    }

    public Optional<String> getJSessionId() {
        return cookie.get(JSESSION_ID_KEY);
    }

    public Map<String, String> getHeader() {
        return Collections.unmodifiableMap(header);
    }
}
