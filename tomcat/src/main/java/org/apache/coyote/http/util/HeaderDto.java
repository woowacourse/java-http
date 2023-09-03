package org.apache.coyote.http.util;

public class HeaderDto {

    private final String key;
    private final String value;

    public HeaderDto(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }
}
