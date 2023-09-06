package org.apache.coyote.http11.handler.mapper;

import nextstep.jwp.controller.index.IndexController;
import nextstep.jwp.controller.index.WelcomeController;
import nextstep.jwp.controller.login.LoginController;
import nextstep.jwp.controller.register.RegisterController;
import nextstep.jwp.controller.resource.ResourceController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.handler.mapper.controller.Controller;
import org.apache.coyote.http11.handler.mapper.controller.RequestPath;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.uri.HttpMethod;

import java.util.Map;

public class HandlerMapper {

    private static final Map<RequestPath, Controller> CONTROLLER_MAP = Map.of(
            RequestPath.WELCOME_GET, new WelcomeController(),
            RequestPath.INDEX_GET, new IndexController(),
            RequestPath.RESOURCE_GET, new ResourceController(),
            RequestPath.LOGIN_POST, new LoginController(new UserService()),
            RequestPath.LOGIN_GET, new LoginController(new UserService()),
            RequestPath.REGISTER_GET, new RegisterController(new UserService()),
            RequestPath.REGISTER_POST, new RegisterController(new UserService())
    );

    public static Controller getController(final HttpRequest httpRequest) {
        if (httpRequest.hasResource()) {
            return CONTROLLER_MAP.get(RequestPath.RESOURCE_GET);
        }

        String path = httpRequest.getPath();
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        RequestPath requestPath = RequestPath.find(path, httpMethod);

        return CONTROLLER_MAP.get(requestPath);
    }
}
