package org.apache.coyote.http11.request;

import java.util.Arrays;

import org.apache.coyote.http11.exception.InvalidUriPath;

public enum RegisteredUri {
    LOGIN("/login"),
    REGISTER("/register");

    private static final String HTML_EXTENSION = ".html";
    private final String path;

    RegisteredUri(final String path) {
        this.path = path;
    }

    public static String getPathWithExtension(String path) {
        final RegisteredUri foundRegisteredUri = Arrays.stream(values())
                .filter(registeredUri -> registeredUri.path.equals(path))
                .findAny()
                .orElseThrow(() -> new InvalidUriPath("등록되지 않은 URI입니다."));
        return foundRegisteredUri.addExtension();
    }

    private String addExtension() {
        return path + HTML_EXTENSION;
    }
}
