package org.apache.coyote.http11.resource;

import java.util.Objects;

public class Cookie {

    private String key;

    private String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isKeyName(String key) {
        return Objects.equals(this.key, key);
    }
}
