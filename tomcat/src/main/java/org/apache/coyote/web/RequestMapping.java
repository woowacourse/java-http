package org.apache.coyote.web;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.UserCreateController;
import nextstep.jwp.controller.UserLoginController;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.SimpleHttpResponse;

public class RequestMapping {

    private static Map<String, Controller> handlers = new HashMap<>();

    static {
        handlers.put("/login", new UserLoginController());
        handlers.put("/register", new UserCreateController());
    }

    public void handle(HttpRequest httpRequest, SimpleHttpResponse httpResponse) {
        if (handlers.containsKey(httpRequest.getRequestUrl())) {
            Controller controller = handlers.get(httpRequest.getRequestUrl());
            controller.service(httpRequest, httpResponse);
            return;
        }
        httpResponse.forward(httpRequest.getRequestUrl());
    }
}
