package org.apache.coyote.http11;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public enum RequestMapping {

    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
    STATIC("static", new StaticController());

    private final String path;
    private final Controller controller;

    RequestMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static RequestMapping from(HttpRequest request) {
        String path = request.path();
        return Arrays.stream(values())
                .filter(requestMapping -> requestMapping.path.equals(path))
                .findAny()
                .orElse(STATIC);
    }

    public HttpResponse service(HttpRequest request) throws Exception {
        HttpResponse response = HttpResponse.ok();
        controller.service(request, response);
        return response;
    }
}
