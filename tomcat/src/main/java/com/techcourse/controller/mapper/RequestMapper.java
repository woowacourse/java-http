package com.techcourse.controller.mapper;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.service.RegisterService;
import com.techcourse.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Mapper;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapper implements Mapper {

    private static final RequestMapper INSTANCE = new RequestMapper();

    private static final Map<String, Controller> handlerMap = new HashMap<>();

    static {
        handlerMap.put("/login", new LoginController(new UserService()));
        handlerMap.put("/register", new RegisterController(new RegisterService()));
        handlerMap.put("/", new RootController());
    }

    public static RequestMapper getInstance() {
        return INSTANCE;
    }

    private RequestMapper() {
    }

    @Override
    public Optional<Controller> getController(HttpRequest request) {
        return Optional.ofNullable(handlerMap.get(request.getUri()));
    }
}
