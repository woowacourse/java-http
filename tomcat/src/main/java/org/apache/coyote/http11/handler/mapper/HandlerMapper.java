package org.apache.coyote.http11.handler.mapper;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.handler.controller.base.Controller;
import org.apache.coyote.http11.handler.controller.base.Dict;
import org.apache.coyote.http11.handler.controller.index.IndexController;
import org.apache.coyote.http11.handler.controller.index.WelcomeController;
import org.apache.coyote.http11.handler.controller.login.LoginController;
import org.apache.coyote.http11.handler.controller.register.RegisterController;
import org.apache.coyote.http11.handler.controller.resource.ResourceController;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class HandlerMapper {

    private static final Map<Dict, Controller> CONTROLLER_MAP = Map.of(
            Dict.WELCOME_GET, new WelcomeController(),
            Dict.INDEX_GET, new IndexController(),

            Dict.RESOURCE_GET, new ResourceController(),

            Dict.LOGIN_POST, new LoginController(new UserService()),
            Dict.LOGIN_GET, new LoginController(new UserService()),

            Dict.REGISTER_GET, new RegisterController(new UserService()),
            Dict.REGISTER_POST, new RegisterController(new UserService())
    );

    public static Controller getController(final HttpRequest httpRequest) {
        if (httpRequest.hasResource()) {
            return CONTROLLER_MAP.get(Dict.RESOURCE_GET);
        }

        String path = httpRequest.getPath();
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        Dict dict = Dict.find(path, httpMethod);

        return CONTROLLER_MAP.get(dict);
    }
}
