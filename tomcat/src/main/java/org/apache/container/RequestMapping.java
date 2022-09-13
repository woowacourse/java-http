package org.apache.container;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.presentation.DefaultPageController;
import nextstep.jwp.presentation.IndexController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import org.apache.coyote.http11.http11request.Http11Request;

public class RequestMapping {

    Map<String, Controller> controllerMapper;

    public RequestMapping() {
        this.controllerMapper = new HashMap<>();
        controllerMapper.put("/", new DefaultPageController());
        controllerMapper.put("/login", new LoginController());
        controllerMapper.put("/index", new IndexController());
        controllerMapper.put("/register", new RegisterController());
    }

    public Controller findController(Http11Request request) {
        return controllerMapper.get(request.getUri());
    }
}
