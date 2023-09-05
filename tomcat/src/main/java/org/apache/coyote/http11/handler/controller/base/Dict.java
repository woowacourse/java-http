package org.apache.coyote.http11.handler.controller.base;

import java.util.Arrays;

public enum Dict {

    INDEX("/index"),
    LOGIN("/login"),
    REGISTER("/register");

    private final String path;

    Dict(final String path) {
        this.path = path;
    }

    public static Dict find(final String path) {
        return Arrays.stream(values())
                .filter(it -> it.getPath().startsWith(path))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 처리할 수 없습니다."));
    }

    public String getPath() {
        return path;
    }
}
