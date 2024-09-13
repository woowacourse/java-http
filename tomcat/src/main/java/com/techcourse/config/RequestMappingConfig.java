package com.techcourse.config;

import java.util.Map;

import org.apache.catalina.servlet.RequestMapping;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;

public class RequestMappingConfig {
    public static RequestMapping getRequestMapping() {
        return new RequestMapping(
                Map.of(
                        "/login", new LoginController(),
                        "/register", new RegisterController()
                )
        );
    }
}
