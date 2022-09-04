package org.apache.coyote.http11.model.session;

import java.util.UUID;

public class Cookie {

    private static final String key = "JSESSIONID";
    private final UUID value;

    public Cookie() {
        this.value = UUID.randomUUID();
    }

    public String getCookieToString() {
        return key + "=" + value.toString();
    }
}
