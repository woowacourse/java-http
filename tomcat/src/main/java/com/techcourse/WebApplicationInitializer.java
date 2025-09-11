package com.techcourse;

import com.techcourse.controller.GreetingController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.model.LoginService;
import com.techcourse.model.RegisterService;
import org.apache.catalina.controller.RequestMapping;

public class WebApplicationInitializer {

    private WebApplicationInitializer() {
    }

    private static final RequestMapping requestMapping = new RequestMapping();

    static {
        requestMapping.addMapping("/login", new LoginController(new LoginService()));
        requestMapping.addMapping("/register", new RegisterController(new RegisterService()));
        requestMapping.addMapping("/", new GreetingController());
    }

    public static RequestMapping getRequestMapping() {
        return requestMapping;
    }
}
