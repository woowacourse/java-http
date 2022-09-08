package org.apache.coyote.http11.model.session;

public class Cookie {

    private final String key;
    private final String value;

    public Cookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getCookieToString() {
        return key + "=" + value.toString();
    }
}
