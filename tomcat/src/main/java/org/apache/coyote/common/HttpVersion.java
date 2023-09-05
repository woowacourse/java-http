package org.apache.coyote.common;

import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.exception.http.HttpVersionNotMatchException;

public enum HttpVersion {

    HTTP_09("HTTP/0.9"),
    HTTP_10("HTTP/1.0"),
    HTTP_11("HTTP/1.1"),
    HTTP_20("HTTP/2"),
    HTTP_30("HTTP/3");

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion from(final String version) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> Objects.equals(httpVersion.version, version))
                .findFirst()
                .orElseThrow(HttpVersionNotMatchException::new);
    }

    public String getVersion() {
        return version;
    }
}
