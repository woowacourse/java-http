package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.error.HttpException;

public enum HttpVersion {
    VERSION_11("HTTP/1.1");

    private final String name;

    HttpVersion(final String name) {
        this.name = name;
    }

    public static HttpVersion pick(final String name) {
        return Arrays.stream(values())
            .filter(httpVersion -> Objects.equals(httpVersion.name, name))
            .findFirst()
            .orElseThrow(() ->
                new HttpException(HttpStatus.HTTP_VERSION_NOT_SUPPORTED,
                    "지원하지 않는 HTTP Version 입니다: " + name));
    }

    public String getName() {
        return name;
    }
}
