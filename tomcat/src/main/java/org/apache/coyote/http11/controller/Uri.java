package org.apache.coyote.http11.controller;

import java.util.Arrays;

public enum Uri {
    ROOT("/", "/"),
    INDEX("/index", "/index.html"),
    LOGIN("/login", "/login.html"),
    UNAUTHORIZED("/401", "/401.html"),
    REGISTER("/register", "/register.html"),
    DEFAULT("", "");

    private final String simplePath;
    private final String fullPath;

    Uri(final String simplePath, final String fullPath) {
        this.simplePath = simplePath;
        this.fullPath = fullPath;
    }

    public static Uri from(final String path) {
        return Arrays.stream(values())
                .filter(uri -> uri.simplePath.equals(path) || uri.fullPath.equals(path))
                .findFirst()
                .orElseGet(() -> DEFAULT);
    }

    public String getSimplePath() {
        return simplePath;
    }

    public String getFullPath() {
        return fullPath;
    }
}
