package nextstep.jwp.web;

import nextstep.jwp.web.exception.NoSuchHttpVersionException;

import java.util.Arrays;

public enum HttpVersion {
    DEFAULT_VERSION("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static HttpVersion of(String value) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.value().equals(value))
                .findFirst()
                .orElseThrow(NoSuchHttpVersionException::new);
    }
}
