package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponseCookies {

    private final Map<String, String> cookies;

    private HttpResponseCookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpResponseCookies empty() {
        return new HttpResponseCookies(new HashMap<>());
    }

    public String get(final String key) {
        return cookies.get(key);
    }

    public String add(final String key, final String value) {
        return cookies.put(key, value);
    }

    public Set<Entry<String, String>> getEntrySet() {
        return cookies.entrySet();
    }
}
