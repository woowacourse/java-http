package org.apache.coyote.http11.controller;

public enum Uri {
    ROOT("/", "/"),
    INDEX("/index", "/index.html"),
    LOGIN("/login", "/login.html"),
    UNAUTHORIZED("/401", "/401.html"),
    REGISTER("/register", "/register.html");

    private final String simplePath;
    private final String fullPath;

    Uri(final String simplePath, final String fullPath) {
        this.simplePath = simplePath;
        this.fullPath = fullPath;
    }

    public String getSimplePath() {
        return simplePath;
    }

    public String getFullPath() {
        return fullPath;
    }
}
