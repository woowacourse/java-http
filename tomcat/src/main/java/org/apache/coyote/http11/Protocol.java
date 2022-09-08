package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.catalina.exception.VersionNotFoundException;

public enum Protocol {
    HTTP1("HTTP/1.0"),
    HTTP1_1("HTTP/1.1"),
    HTTP2("HTTP/2.0");

    private final String value;

    Protocol(final String value) {
        this.value = value;
    }

    public static Protocol of(final String versionString) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(versionString))
                .findFirst()
                .orElseThrow(VersionNotFoundException::new);
    }

    public String getValue() {
        return value;
    }
}
