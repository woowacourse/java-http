package nextstep.jwp.http.entity;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public static HttpVersion of(String name) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.hasName(name))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean hasName(String name) {
        return this.name.equals(name);
    }
}
