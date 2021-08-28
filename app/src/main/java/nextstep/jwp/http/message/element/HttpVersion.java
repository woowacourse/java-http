package nextstep.jwp.http.message.element;

import java.util.Arrays;
import java.util.Objects;

public enum HttpVersion {
    HTTP1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion from(String version) {
        return Arrays.stream(values())
            .filter(value -> Objects.equals(value.asString(), version.toUpperCase()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Cannot Support Version"));
    }

    public String asString() {
        return version;
    }
}
