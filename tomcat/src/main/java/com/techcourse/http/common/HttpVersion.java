package com.techcourse.http.common;


import com.techcourse.exception.NotFoundException;
import java.util.Arrays;
import java.util.Objects;

public enum HttpVersion {

    HTTP_1_0("1.0"),
    HTTP_1_1("1.1"),
    HTTP_2("2"),
    HTTP_3("3"),
    ;

    private static final String HTTP_VERSION_PREFIX = "HTTP/";

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion from(final String protocolString) {
        Objects.requireNonNull(protocolString);

        return Arrays.stream(values())
                .filter(version -> version.toProtocolString().equals(protocolString))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 http method 입니다."));
    }

    public String toProtocolString() {
        return HTTP_VERSION_PREFIX + version;
    }
}
