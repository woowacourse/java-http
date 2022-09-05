package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.exception.NoSuchHttpVersionException;

public enum HttpVersion {

    HTTP11("HTTP/1.1");

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion from(final String httpVersion) {
        return Arrays.stream(values())
                .filter(value -> value.getValue().equals(httpVersion))
                .findFirst()
                .orElseThrow(NoSuchHttpVersionException::new);
    }

    public String getValue() {
        return value;
    }
}
