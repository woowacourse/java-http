package com.techcourse.api;

import com.techcourse.api.controller.LoginController;
import com.techcourse.api.controller.RegisterController;
import com.techcourse.api.controller.RootController;
import org.apache.catalina.Manager;

public class RequestMappingFactory {

    private RequestMappingFactory() {
    }

    public static RequestMapping create(final Manager sessionManager) {
        final RequestMapping requestMapping = new RequestMapping();

        final RootController rootController = new RootController();
        final LoginController loginController = new LoginController(sessionManager);
        final RegisterController registerController = new RegisterController();

        requestMapping.add("GET", "/", rootController);
        requestMapping.add("GET", "/index.html", rootController);
        requestMapping.add("GET", "/login", loginController);
        requestMapping.add("POST", "/login", loginController);
        requestMapping.add("GET", "/register", registerController);
        requestMapping.add("POST", "/register", registerController);
        return requestMapping;
    }
}
