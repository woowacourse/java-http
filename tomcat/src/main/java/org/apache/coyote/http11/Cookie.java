package org.apache.coyote.http11;

import java.util.UUID;

public class Cookie {

    private static final String key = "JSESSIONID";
    private final UUID value;

    public Cookie() {
        this.value = UUID.randomUUID();
    }

    public String getValue() {
        return value.toString();
    }

    @Override
    public String toString() {
        return key + "=" + value.toString();
    }
}
