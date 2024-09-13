package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final String name;
    private final String value;
    private final Map<String, String> attributes;
    private boolean httpOnly;
    private boolean secure;

    public HttpCookie(String name, String value, Map<String, String> attributes) {
        this(name, value, attributes, false, false);
    }

    public HttpCookie(String name, String value, Map<String, String> attributes, boolean httpOnly, boolean secure) {
        this.name = name;
        this.value = value;
        this.attributes = new HashMap<>(attributes);
        this.httpOnly = httpOnly;
        this.secure = secure;
    }

    public HttpCookie setHttpOnly() {
        this.httpOnly = true;
        return this;
    }

    public HttpCookie setSecure() {
        this.secure = true;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isSecure() {
        return secure;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }
}
