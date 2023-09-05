package org.apache.coyote.http11.handler.mapper;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.handler.controller.base.Controller;
import org.apache.coyote.http11.handler.controller.base.Dict;
import org.apache.coyote.http11.handler.controller.index.IndexController;
import org.apache.coyote.http11.handler.controller.login.LoginController;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class HandlerMapper {

    private static final Map<Dict, Controller> CONTROLLER_MAP = Map.of(
            Dict.INDEX, new IndexController(),
            Dict.LOGIN, new LoginController(new UserService()),
            Dict.REGISTER, new LoginController(new UserService())
    );

    public static Controller getController(final HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        Dict dict = Dict.find(path);
        return CONTROLLER_MAP.get(dict);
    }
}
