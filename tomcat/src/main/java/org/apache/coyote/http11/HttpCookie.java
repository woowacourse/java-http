package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public record HttpCookie(Map<String, String> pairs) {

    public HttpCookie {
        pairs = Map.copyOf(pairs);
    }

    public static HttpCookie from(final String rawCookie) {
        final Map<String, String> pairs = new HashMap<>();
        if (rawCookie == null) {
            return new HttpCookie(pairs);
        }
        for (String pair : rawCookie.split(";\\s*"))  {
            final int splitIndex = pair.indexOf("=");
            final String key = pair.substring(0, splitIndex);
            final String value = pair.substring(splitIndex + 1);
            pairs.put(key, value);
        }
        return new HttpCookie(pairs);
    }

    public boolean contains(final String name) {
        return pairs.containsKey(name);
    }

    public String get(final String name) {
        return pairs.get(name);
    }
}
