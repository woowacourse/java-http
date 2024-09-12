package com.techcourse.controller.mapper;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.service.RegisterService;
import com.techcourse.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.DefaultController;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapper {

    private static final Map<String, Controller> handlerMap = new HashMap<>();

    static {
        handlerMap.put("/login", new LoginController(new UserService()));
        handlerMap.put("/register", new RegisterController(new RegisterService()));
        handlerMap.put("/", new RootController());
    }

    private RequestMapper() {
    }

    public static Controller getController(HttpRequest request) {
        if (request.isStaticResource()) {
            return new DefaultController();
        }

        return handlerMap.getOrDefault(request.getUri(), new NotFoundController());
    }
}
