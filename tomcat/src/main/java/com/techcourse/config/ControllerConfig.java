package com.techcourse.config;


import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.coyote.http11.RequestMapping;

import java.util.Map;

public final class ControllerConfig {

    public RequestMapping requestMapping() {
        return new RequestMapping(Map.of(
                "/index", new IndexController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        ));
    }
}