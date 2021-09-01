package nextstep.jwp.http.common;

import java.util.Arrays;
import nextstep.jwp.exception.HTTPVersionNotSupportedException;

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
            .orElseThrow(HTTPVersionNotSupportedException::new);
    }

    public String getVersion() {
        return value;
    }
}
