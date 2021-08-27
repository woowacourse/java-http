package nextstep.jwp.http.common;

import java.util.Arrays;
import nextstep.jwp.exception.NotAllowedHttpVersionException;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String version) {
        this.value = version;
    }

    public static HttpVersion matchOf(String requestVersion) {
        return Arrays.stream(values())
            .filter(version -> requestVersion.equals(version.value))
            .findAny()
            .orElseThrow(NotAllowedHttpVersionException::new);
    }

    public String getValue() {
        return value;
    }
}
