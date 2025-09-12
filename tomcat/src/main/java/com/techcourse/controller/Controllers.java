package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.controller.Controller;

public final class Controllers {

    private final Map<String, Controller> controllers;

    public Controllers() {
        this.controllers = Map.of(
                "/", new HomeController(),
                "/css/styles.css", new StaticFileController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    public Controller get(final String url) {
        return controllers.get(url);
    }
}
