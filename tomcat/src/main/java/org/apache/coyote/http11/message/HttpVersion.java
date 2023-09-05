package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpVersion {

    V_1_0("HTTP/1.0"),
    V_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String target) {
        return Arrays.stream(values())
                .filter(each -> each.value.equals(target))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public String toString() {
        return value;
    }
}
