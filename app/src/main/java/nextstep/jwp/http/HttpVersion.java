package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion matchOf(String requestVersion) {
        return Arrays.stream(values())
            .filter(version -> requestVersion.equals(version.value))
            .findAny()
            .orElseThrow(RuntimeException::new);
    }
}
