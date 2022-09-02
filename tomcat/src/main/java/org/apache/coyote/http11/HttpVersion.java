package org.apache.coyote.http11;

import java.util.Arrays;
import nextstep.jwp.exception.NotFoundException;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion from(final String httpVersion) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(httpVersion))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("HTTP-Version not found"));
    }

    public String getValue() {
        return value;
    }
}
