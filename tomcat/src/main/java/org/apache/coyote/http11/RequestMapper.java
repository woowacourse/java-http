package org.apache.coyote.http11;

import com.techcourse.controller.Controller;

public class RequestMapper {

    private final Controller controller;

    public RequestMapper() {
        this.controller = new Controller();
    }

    public String requestMapping(HttpRequest request) {
        String method = request.getMethod();
        String path = request.getPath();
        if (method.equals("GET") && path.equals("/login")) {
            return controller.getLogin(request);
        }
        return request.getPath();
    }
}
