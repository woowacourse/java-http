package com.techcourse.config;

import java.util.Map;

import org.apache.catalina.servlet.RequestMapping;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;

public class RequestMappingConfig {
    public static RequestMapping getRequestMapping() {
        var staticResourceController = new StaticResourceController();
        return new RequestMapping(
                Map.of(
                        "/login", new LoginController(),
                        "/register", new RegisterController(),
                        "/", staticResourceController,
                        ".*\\.(js|html|css)$", staticResourceController
                )
        );
    }
}
