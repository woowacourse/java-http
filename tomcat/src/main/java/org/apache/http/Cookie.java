package org.apache.http;

public class Cookie {

    private final String key;
    private final String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String makeCookieLine() {
        return key + "=" + value + ";";
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
