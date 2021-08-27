package nextstep.jwp.httpserver.domain;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2.0");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion version(String value) {
        return Arrays.stream(values())
                     .filter(v -> v.version.equals(value))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 버전입니다."));
    }

    public String getVersion() {
        return version;
    }
}
