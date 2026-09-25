package com.techcourse.web;

import java.util.Arrays;
import java.util.Optional;

public enum Route {

    HOME("/"),
    LOGIN("/login"),
    REGISTER("/register");

    private final String path;

    Route(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static Optional<Route> fromPath(String path) {
        return Arrays.stream(values())
                .filter(route -> route.path.equals(path))
                .findFirst();
    }
}
