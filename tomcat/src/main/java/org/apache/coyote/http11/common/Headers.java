package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.Constants.EMPTY;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Headers {

    private static final String DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, String> items = new HashMap<>();

    public Headers() {
        this(Collections.emptyMap());
    }

    private Headers(final Map<String, String> items) {
        this.items.putAll(items);
    }

    public void addHeader(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }
        final String[] header = line.split(DELIMITER);
        items.put(header[KEY_INDEX].strip(), header[VALUE_INDEX].strip());
    }

    public void addHeader(final String key, final String value) {
        items.put(key, value);
    }

    public HttpCookie parseCookie() {
        final String cookie = items.getOrDefault(COOKIE_HEADER, EMPTY);
        return HttpCookie.from(cookie);
    }

    public String get(final String key) {
        return items.getOrDefault(key, EMPTY);
    }

    public Map<String, String> getItems() {
        return items;
    }
}
