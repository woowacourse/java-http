package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final String name;
    private final String value;
    private final Map<String, String> attributes;

    public HttpCookie(String name, String value, Map<String, String> attributes) {
        this.name = name;
        this.value = value;
        this.attributes = new HashMap<>(attributes);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }
}
