package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public static Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }
        if (path.equals("/")) {
            return new HomeController();
        }

        if (path.startsWith("/login")) {
            return new LoginController();
        }
        if (path.equals("/register")) {
            return new RegisterController();
        }
        return new PageController();
    }
}
