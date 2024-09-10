package org.apache.coyote.http11;

public class Cookie {

    private final String key;
    private final String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKeyValue() {
        return key + "=" + value;
    }

    public String getValue() {
        return value;
    }
}
