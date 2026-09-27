package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.StaticResource;

public final class WebApplication {

    private WebApplication() {
    }

    public static RequestMapping createRequestMapping() {
        StaticResource staticResource = new StaticResource();
        RequestMapping requestMapping = new RequestMapping(new StaticResourceController(staticResource));

        requestMapping.add("/", new RootController());
        requestMapping.add("/login", new LoginController(staticResource));
        requestMapping.add("/register", new RegisterController(staticResource));
        return requestMapping;
    }
}
