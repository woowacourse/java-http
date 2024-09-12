package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RequestMapping {

    public static Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }
        else if (path.equals("/")) {
            return new HomeController();
        }
        else if (path.startsWith("/login")) {
            return new LoginController();
        }
        else if (path.equals("/register")) {
            return new RegisterController();
        }
        else return new PageController();
    }
}
