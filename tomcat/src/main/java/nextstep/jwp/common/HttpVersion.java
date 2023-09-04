package nextstep.jwp.common;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion of(final String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equals(version))
                .findFirst()
                .orElseThrow();
    }

    public String getVersion() {
        return version;
    }
}
