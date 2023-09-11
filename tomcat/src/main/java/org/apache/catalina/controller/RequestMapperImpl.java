package org.apache.catalina.controller;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.RequestMapper;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.List;

public class RequestMapperImpl implements RequestMapper {

    private static final RequestMapper instance = new RequestMapperImpl();

    private static final List<Controller> CONTROLLERS;

    static {
        CONTROLLERS = List.of(
                new LoginController(),
                new RegisterController()
        );
    }

    private RequestMapperImpl() {
    }

    public static RequestMapper getInstance() {
        return instance;
    }

    public Controller getController(final HttpRequest request) {
        return CONTROLLERS.stream()
                .filter(controller -> controller.isMappedController(request))
                .findAny()
                .orElse(DefaultController.getInstance());
    }
}
