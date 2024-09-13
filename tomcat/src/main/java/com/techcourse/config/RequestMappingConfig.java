package com.techcourse.config;

import java.util.Map;

import org.apache.catalina.servlet.RequestMapping;

import com.techcourse.controller.LoginController;

public class RequestMappingConfig {
    public static RequestMapping getRequestMapping() {
        return new RequestMapping(
                Map.of(
                        "/login", new LoginController()
                )
        );
    }
}
