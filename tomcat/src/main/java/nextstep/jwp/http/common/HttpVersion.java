package nextstep.jwp.http.common;

import java.util.Arrays;

public enum HttpVersion {
    V1_0("HTTP/1.0"),
    V1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String input) {
        return Arrays.stream(values())
                .filter(value -> value.getValue().equals(input))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getValue() {
        return value;
    }

}
