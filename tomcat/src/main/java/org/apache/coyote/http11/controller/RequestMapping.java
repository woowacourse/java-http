package org.apache.coyote.http11.controller;

import java.util.Arrays;

import org.apache.coyote.http11.request.HttpRequest;

public enum RequestMapping {
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
    INDEX("/index.html", new IndexController()),
    RESOURCE("", new ResourceController()),
    ;

    private final String path;
    private final Controller matchingController;

    RequestMapping(String path, Controller matchingController) {
        this.path = path;
        this.matchingController = matchingController;
    }

    public static Controller getController(HttpRequest request) {
        RequestMapping requestMapping = Arrays.stream(values())
            .filter(value -> value.path.equals(request.getPath()))
            .findFirst()
            .orElse(RESOURCE);

        return requestMapping.matchingController;
    }
}
