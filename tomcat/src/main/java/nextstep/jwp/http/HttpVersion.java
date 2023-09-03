package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpVersion {
    V1_0("HTTP/1.0"),
    V1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    // TODO: 2023/09/04 예외처리 구현

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
